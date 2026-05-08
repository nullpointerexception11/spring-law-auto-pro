package com.lawauto.backend.petition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawauto.backend.operations.FileObjectService;
import com.lawauto.backend.operations.OperationDtos.CreateFileObjectRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PetitionDraftExportService {
    private final NamedParameterJdbcTemplate jdbc;
    private final FileObjectService fileObjectService;
    private final PetitionDocumentRenderer renderer;
    private final LocalExportStorageService storageService;
    private final ObjectMapper objectMapper;
    private final PetitionPlaceholderService placeholderService;

    public PetitionDraftExportService(
            NamedParameterJdbcTemplate jdbc,
            FileObjectService fileObjectService,
            PetitionDocumentRenderer renderer,
            LocalExportStorageService storageService,
            ObjectMapper objectMapper,
            PetitionPlaceholderService placeholderService
    ) {
        this.jdbc = jdbc;
        this.fileObjectService = fileObjectService;
        this.renderer = renderer;
        this.storageService = storageService;
        this.objectMapper = objectMapper;
        this.placeholderService = placeholderService;
    }

    public ExportResult export(UUID orgId, UUID draftId, String format) {
        log.info("Starting petition export for draft [{}] in format [{}]", draftId, format);
        String normalized = normalizeFormat(format);
        DraftProjection draft = findDraft(orgId, draftId);
        if (draft == null) throw new IllegalArgumentException("Petition draft not found");

        String extension = normalized;
        String mimeType = switch (normalized) {
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "pdf" -> "application/pdf";
            default -> throw new IllegalArgumentException("Unsupported export format");
        };

        String safeTitle = draft.title().replaceAll("[^a-zA-Z0-9_-]+", "_");
        if (safeTitle.isBlank()) safeTitle = "petition_draft";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = safeTitle + "_" + timestamp + "." + extension;
        String storageKey = "exports/" + orgId + "/petition-drafts/" + draft.id() + "/" + fileName;

        ExportPayload payload = new ExportPayload(
                draft.id(),
                draft.caseId(),
                draft.title(),
                draft.content(),
                parseSections(draft.caseId(), draft.templateStructureJson(), draft.content(), draft.sectionValuesJson())
        );

        byte[] bytes;
        if (draft.templateFileId() != null) {
            String templateKey = fileObjectService.getStorageKey(draft.templateFileId());
            byte[] templateBytes = storageService.read(templateKey);
            Map<String, String> placeholders = placeholderService.getPlaceholders(draft.caseId());
            bytes = renderer.renderDocxFromTemplate(templateBytes, placeholders);
        } else {
            bytes = normalized.equals("docx") ? renderer.renderDocx(payload) : renderer.renderPdf(payload);
        }
        
        storageService.save(storageKey, bytes);
        int sizeBytes = bytes.length;
        String sha256 = sha256Hex(bytes);

        UUID fileId = fileObjectService.create(new CreateFileObjectRequest(
                orgId,
                storageKey,
                fileName,
                mimeType,
                sizeBytes,
                sha256
        ));

        return new ExportResult(fileId, fileName, mimeType, storageKey, normalized);
    }

    private DraftProjection findDraft(UUID orgId, UUID draftId) {
        String sql = """
                select d."id",d."orgId",d."caseId",d."title",d."content",d."sectionValuesJson",
                       t."structureJson" as "templateStructureJson", t."template_file_id"
                from "PetitionDraft" d
                left join "PetitionTemplate" t on t."id" = d."templateId"
                where d."id"=:id and d."orgId"=:orgId
                """;
        return jdbc.query(sql, new MapSqlParameterSource().addValue("id", draftId).addValue("orgId", orgId), rs -> {
            if (!rs.next()) return null;
            return new DraftProjection(
                    java.util.Objects.requireNonNull(rs.getObject("id", UUID.class)),
                    java.util.Objects.requireNonNull(rs.getObject("orgId", UUID.class)),
                    java.util.Objects.requireNonNull(rs.getObject("caseId", UUID.class)),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("sectionValuesJson"),
                    rs.getString("templateStructureJson"),
                    rs.getObject("template_file_id", UUID.class)
            );
        });
    }

    private List<TemplateSection> parseSections(UUID caseId, String structureJson, String draftContent, String sectionValuesJson) {
        List<TemplateSection> sections = new ArrayList<>();
        Map<String, String> placeholders = placeholderService.getPlaceholders(caseId);

        if (structureJson == null || structureJson.isBlank()) {
            String finalContent = placeholderService.replace(draftContent, placeholders);
            return List.of(new TemplateSection("Icerik", finalContent));
        }
        try {
            JsonNode root = objectMapper.readTree(structureJson);
            JsonNode valuesRoot = sectionValuesJson == null || sectionValuesJson.isBlank()
                    ? objectMapper.createObjectNode()
                    : objectMapper.readTree(sectionValuesJson);
            JsonNode array = root.get("sections");
            if (array != null && array.isArray()) {
                for (JsonNode item : array) {
                    String key = item.hasNonNull("key") ? item.get("key").asText() : null;
                    String title = item.hasNonNull("title") ? item.get("title").asText() : "Bolum";
                    String body = item.hasNonNull("content") ? item.get("content").asText() : "";
                    if (key != null && valuesRoot.has(key)) {
                        body = valuesRoot.get(key).asText("");
                    }
                    if (body.contains("{{body}}")) {
                        body = body.replace("{{body}}", draftContent == null ? "" : draftContent);
                    }
                    
                    // Apply dynamic placeholders from Insurance/Case/Client
                    body = placeholderService.replace(body, placeholders);
                    
                    sections.add(new TemplateSection(title, body));
                }
            }
        } catch (Exception ignored) {
            String finalContent = placeholderService.replace(draftContent, placeholders);
            return List.of(new TemplateSection("Icerik", finalContent));
        }
        if (sections.isEmpty()) {
            String finalContent = placeholderService.replace(draftContent, placeholders);
            sections.add(new TemplateSection("Icerik", finalContent));
        }
        return sections;
    }

    public UUID findCaseId(UUID orgId, UUID draftId) {
        DraftProjection draft = findDraft(orgId, draftId);
        if (draft == null) throw new IllegalArgumentException("Petition draft not found");
        return draft.caseId();
    }

    private String normalizeFormat(String format) {
        if (format == null || format.isBlank()) throw new IllegalArgumentException("format is required");
        String normalized = format.trim().toLowerCase(Locale.ROOT);
        if (!normalized.equals("docx") && !normalized.equals("pdf")) {
            throw new IllegalArgumentException("Unsupported export format");
        }
        return normalized;
    }

    private String sha256Hex(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private record DraftProjection(
            UUID id,
            UUID orgId,
            UUID caseId,
            String title,
            String content,
            String sectionValuesJson,
            String templateStructureJson,
            UUID templateFileId
    ) {}

    public record ExportPayload(UUID draftId, UUID caseId, String title, String content, List<TemplateSection> sections) {}
    public record TemplateSection(String title, String content) {}
    public record ExportResult(UUID fileId, String fileName, String mimeType, String storageKey, String format) {}
}

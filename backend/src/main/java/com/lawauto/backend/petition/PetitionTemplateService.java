package com.lawauto.backend.petition;

import com.lawauto.backend.auth.AuthPrincipal;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PetitionTemplateService {
    private static final Set<String> ALLOWED_SECTION_MODES = Set.of("INPUT", "BODY", "AUTO");
    private final PetitionTemplateRepository repository;
    private final ObjectMapper objectMapper;

    public PetitionTemplateService(PetitionTemplateRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public List<PetitionTemplateEntity> listByOrg(UUID orgId) {
        return repository.findByOrgIdOrderByNameAscVersionDesc(orgId);
    }

    @Transactional
    public UUID create(AuthPrincipal principal, PetitionTemplateController.CreatePetitionTemplateRequest req) {
        validateStructureJson(req.structureJson());
        PetitionTemplateEntity entity = new PetitionTemplateEntity();
        entity.setId(UUID.randomUUID());
        entity.setOrgId(req.orgId());
        entity.setName(req.name());
        entity.setVersion(req.version() == null ? 1 : req.version());
        entity.setActive(Boolean.TRUE.equals(req.isActive()));
        entity.setStructureJson(req.structureJson());
        entity.setCreatedByUserId(principal.userId());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public void update(UUID orgId, UUID templateId, PetitionTemplateController.UpdatePetitionTemplateRequest req) {
        PetitionTemplateEntity entity = repository.findByIdAndOrgId(templateId, orgId)
                .orElseThrow(() -> new IllegalArgumentException("Petition template not found"));
        if (req.name() != null && !req.name().isBlank()) entity.setName(req.name().trim());
        if (req.structureJson() != null && !req.structureJson().isBlank()) {
            validateStructureJson(req.structureJson());
            entity.setStructureJson(req.structureJson());
        }
        if (req.version() != null && req.version() > 0) entity.setVersion(req.version());
        entity.setUpdatedAt(LocalDateTime.now());
        repository.save(entity);
    }

    @Transactional
    public void activate(UUID orgId, UUID templateId) {
        PetitionTemplateEntity selected = repository.findByIdAndOrgId(templateId, orgId)
                .orElseThrow(() -> new IllegalArgumentException("Petition template not found"));
        List<PetitionTemplateEntity> all = repository.findByOrgIdOrderByNameAscVersionDesc(orgId);
        for (PetitionTemplateEntity item : all) {
            boolean active = item.getId().equals(templateId);
            if (item.isActive() != active) {
                item.setActive(active);
                item.setUpdatedAt(LocalDateTime.now());
                repository.save(item);
            }
        }
        if (!selected.isActive()) {
            selected.setActive(true);
            selected.setUpdatedAt(LocalDateTime.now());
            repository.save(selected);
        }
    }

    private void validateStructureJson(String structureJson) {
        try {
            JsonNode root = objectMapper.readTree(structureJson);
            if (!root.has("version") || root.get("version").asInt() != 1) {
                throw new IllegalArgumentException("Invalid template: version must be 1");
            }
            if (!root.hasNonNull("type") || !"PETITION_TEMPLATE".equals(root.get("type").asText())) {
                throw new IllegalArgumentException("Invalid template: type must be PETITION_TEMPLATE");
            }
            JsonNode sections = root.get("sections");
            if (sections == null || !sections.isArray() || sections.isEmpty()) {
                throw new IllegalArgumentException("Invalid template: sections must be non-empty array");
            }

            Set<String> keys = new HashSet<>();
            for (JsonNode section : sections) {
                if (!section.hasNonNull("key") || section.get("key").asText().isBlank()) {
                    throw new IllegalArgumentException("Invalid template: each section requires key");
                }
                String key = section.get("key").asText().trim();
                if (!keys.add(key)) {
                    throw new IllegalArgumentException("Invalid template: duplicate section key: " + key);
                }
                if (!section.hasNonNull("title") || section.get("title").asText().isBlank()) {
                    throw new IllegalArgumentException("Invalid template: each section requires title");
                }
                if (!section.hasNonNull("mode")) {
                    throw new IllegalArgumentException("Invalid template: each section requires mode");
                }
                String mode = section.get("mode").asText().trim().toUpperCase();
                if (!ALLOWED_SECTION_MODES.contains(mode)) {
                    throw new IllegalArgumentException("Invalid template: unsupported mode " + mode);
                }
            }
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid template: malformed structureJson");
        }
    }
}

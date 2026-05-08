package com.lawauto.backend.petition;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class PetitionTemplateService {
    private static final Set<String> ALLOWED_SECTION_MODES = Set.of("INPUT", "BODY", "AUTO");
    private final PetitionTemplateRepository repository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public PetitionTemplateService(
            PetitionTemplateRepository repository, 
            OrgRepository orgRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.orgRepository = orgRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "petitionTemplates", key = "#orgId")
    public List<PetitionTemplateDto> listByOrg(UUID orgId) {
        log.info("Listing petition templates for org [{}]", orgId);
        return repository.findByOrgIdOrderByNameAscVersionDesc(orgId).stream()
                .map(PetitionTemplateDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "petitionTemplates", key = "#req.orgId()")
    public UUID create(AuthPrincipal principal, PetitionTemplateController.CreatePetitionTemplateRequest req) {
        log.info("Creating new petition template [{}] for org [{}]", req.name(), req.orgId());
        validateStructureJson(req.structureJson());
        PetitionTemplate entity = new PetitionTemplate();
        entity.setId(UUID.randomUUID());
        entity.setOrg(orgRepository.getReferenceById(req.orgId()));
        entity.setName(req.name());
        entity.setVersion(req.version() == null ? 1 : req.version());
        entity.setIsActive(Boolean.TRUE.equals(req.isActive()));
        entity.setStructureJson(req.structureJson());
        entity.setCreatedBy(userRepository.getReferenceById(principal.userId()));
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        repository.save(entity);
        return entity.getId();
    }

    @Transactional
    @CacheEvict(value = "petitionTemplates", key = "#orgId")
    public void update(UUID orgId, UUID templateId, PetitionTemplateController.UpdatePetitionTemplateRequest req) {
        log.info("Updating petition template [{}] for org [{}]", templateId, orgId);
        PetitionTemplate entity = repository.findByIdAndOrgId(templateId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Petition template not found"));
        if (req.name() != null && !req.name().isBlank()) entity.setName(req.name().trim());
        if (req.structureJson() != null && !req.structureJson().isBlank()) {
            validateStructureJson(req.structureJson());
            entity.setStructureJson(req.structureJson());
        }
        if (req.version() != null && req.version() > 0) entity.setVersion(req.version());
        entity.setUpdatedAt(OffsetDateTime.now());
        repository.save(entity);
    }

    @Transactional
    @CacheEvict(value = "petitionTemplates", key = "#orgId")
    public void activate(UUID orgId, UUID templateId) {
        log.info("Activating petition template [{}] for org [{}]", templateId, orgId);
        PetitionTemplate selected = repository.findByIdAndOrgId(templateId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Petition template not found"));
        List<PetitionTemplate> all = repository.findByOrgIdOrderByNameAscVersionDesc(orgId);
        for (PetitionTemplate item : all) {
            boolean active = item.getId().equals(templateId);
            if (Boolean.TRUE.equals(item.getIsActive()) != active) {
                item.setIsActive(active);
                item.setUpdatedAt(OffsetDateTime.now());
                repository.save(item);
            }
        }
        if (!Boolean.TRUE.equals(selected.getIsActive())) {
            selected.setIsActive(true);
            selected.setUpdatedAt(OffsetDateTime.now());
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
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid template: malformed structureJson");
        }
    }
}

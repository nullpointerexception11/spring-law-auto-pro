package com.lawauto.backend.petition;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class PetitionTemplateDto {
    private UUID id;
    private UUID orgId;
    private String name;
    private int version;
    private boolean isActive;
    private String structureJson;
    private UUID createdByUserId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public static PetitionTemplateDto fromEntity(PetitionTemplate entity) {
        if (entity == null) {
            return null;
        }
        return PetitionTemplateDto.builder()
                .id(entity.getId())
                .orgId(entity.getOrg() != null ? entity.getOrg().getId() : null)
                .name(entity.getName())
                .version(entity.getVersion())
                .isActive(entity.getIsActive() != null ? entity.getIsActive() : false)
                .structureJson(entity.getStructureJson())
                .createdByUserId(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

package com.lawauto.backend.cases;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CaseResponseDto {
    private UUID id;
    private UUID orgId;
    private UUID clientId;
    private String title;
    private CaseStatus status;

    public static CaseResponseDto fromEntity(CaseEntity caseEntity) {
        if (caseEntity == null) {
            return null;
        }
        return CaseResponseDto.builder()
                .id(caseEntity.getId())
                .orgId(caseEntity.getOrgId())
                .clientId(caseEntity.getClientId())
                .title(caseEntity.getTitle())
                .status(caseEntity.getStatus())
                .build();
    }
}

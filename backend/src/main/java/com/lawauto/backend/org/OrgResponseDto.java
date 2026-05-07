package com.lawauto.backend.org;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrgResponseDto {
    private UUID id;
    private String name;
    private LocalDateTime createdAt;

    public static OrgResponseDto fromEntity(Org org) {
        if (org == null) {
            return null;
        }
        return OrgResponseDto.builder()
                .id(org.getId())
                .name(org.getName())
                .createdAt(org.getCreatedAt())
                .build();
    }
}

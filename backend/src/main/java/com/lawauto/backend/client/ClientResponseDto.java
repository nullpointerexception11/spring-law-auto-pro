package com.lawauto.backend.client;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ClientResponseDto {
    private UUID id;
    private UUID orgId;
    private String fullName;
    private String phone;
    private String email;

    public static ClientResponseDto fromEntity(Client client) {
        if (client == null) {
            return null;
        }
        return ClientResponseDto.builder()
                .id(client.getId())
                .orgId(client.getOrgId())
                .fullName(client.getFullName())
                .phone(client.getPhone())
                .email(client.getEmail())
                .build();
    }
}

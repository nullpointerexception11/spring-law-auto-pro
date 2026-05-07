package com.lawauto.backend.auth;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthResponseDto {
    private String token;
    private String refreshToken;
    private UUID userId;
    private UUID orgId;
    private String email;
    private String role;
}

package com.lawauto.backend.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthResponseDto {
    @JsonProperty("token")
    private String token;
    
    @JsonProperty("refreshToken")
    private String refreshToken;
    
    @JsonProperty("userId")
    private UUID userId;
    
    @JsonProperty("orgId")
    private UUID orgId;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("role")
    private String role;
}

package com.lawauto.backend.auth;

import java.time.LocalDateTime;
import java.util.UUID;

public record RefreshTokenRecord(
        UUID id,
        UUID orgId,
        UUID userId,
        String tokenHash,
        LocalDateTime expiresAt,
        LocalDateTime revokedAt,
        LocalDateTime createdAt
) {}

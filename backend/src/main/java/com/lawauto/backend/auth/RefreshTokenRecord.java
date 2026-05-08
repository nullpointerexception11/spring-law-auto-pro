package com.lawauto.backend.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RefreshTokenRecord(
        UUID id,
        UUID orgId,
        UUID userId,
        String tokenHash,
        OffsetDateTime expiresAt,
        OffsetDateTime revokedAt,
        OffsetDateTime createdAt
) {}

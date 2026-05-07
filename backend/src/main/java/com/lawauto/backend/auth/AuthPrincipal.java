package com.lawauto.backend.auth;

import java.util.UUID;

public record AuthPrincipal(
        UUID userId,
        UUID orgId,
        String role,
        String email
) {
}

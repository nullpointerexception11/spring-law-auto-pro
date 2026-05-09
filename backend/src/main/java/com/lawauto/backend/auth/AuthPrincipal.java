package com.lawauto.backend.auth;

import com.lawauto.backend.user.RoleKey;
import java.util.Set;
import java.util.UUID;

/**
 * Immutable record representing the currently authenticated user.
 * Acts as the 'wallet' of permissions across the application.
 */
public record AuthPrincipal(
    UUID userId,
    UUID orgId,
    Set<RoleKey> roles
) {
    public boolean hasRole(RoleKey role) {
        return roles != null && roles.contains(role);
    }

    public boolean isPlatformAdmin() {
        return hasRole(RoleKey.PLATFORM_ADMIN);
    }

    public boolean isOrgAdmin() {
        return hasRole(RoleKey.ORG_ADMIN);
    }

    public boolean isInternal() {
        return roles != null && roles.stream().anyMatch(RoleKey::isInternal);
    }
}

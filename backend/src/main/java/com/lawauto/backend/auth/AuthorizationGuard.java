package com.lawauto.backend.auth;

import com.lawauto.backend.user.RoleKey;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class AuthorizationGuard {

    /**
     * Retrieves the current authenticated principal from the SecurityContext.
     */
    public AuthPrincipal currentPrincipal() {
        // TODO: Extract from SecurityContextHolder once JWT filter is integrated
        return null;
    }

    /**
     * Ensures the current user belongs to the requested Organization.
     * Prevents cross-tenant data leakage.
     */
    public void requireOrg(UUID orgId) {
        AuthPrincipal principal = currentPrincipal();
        if (principal == null || !orgId.equals(principal.orgId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Organization mismatch or not authenticated");
        }
    }

    /**
     * Ensures the current user possesses at least one of the required roles.
     */
    public void requireAnyRole(RoleKey... requiredRoles) {
        AuthPrincipal principal = currentPrincipal();
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        
        for (RoleKey role : requiredRoles) {
            if (principal.hasRole(role)) return;
        }
        
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient role privileges");
    }
}

package com.lawauto.backend.auth;

import java.util.Set;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationGuard {

    public AuthPrincipal currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal principal)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        return principal;
    }

    public void requireOrg(UUID orgId) {
        AuthPrincipal principal = currentPrincipal();
        if (!principal.orgId().equals(orgId)) {
            throw new IllegalArgumentException("Forbidden: org scope mismatch");
        }
    }

    public void requireRole(String... roles) {
        AuthPrincipal principal = currentPrincipal();
        Set<String> allowed = Set.of(roles);
        if (!allowed.contains(principal.role())) {
            throw new IllegalArgumentException("Forbidden: insufficient role");
        }
    }
}

package com.lawauto.backend.operations;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.user.RoleKey;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class OperationAccessGuard {

    /**
     * Enforces data-level authorization to ensure the user has access to a specific Matter.
     * PLATFORM_ADMIN and ORG_ADMIN have global access within their tenant.
     * For other roles, this guard will verify specific assignments.
     */
    public void requireMatterAccess(AuthPrincipal principal, UUID matterId) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        
        // Global admins bypass specific matter assignment checks
        if (principal.hasRole(RoleKey.ORG_ADMIN) || principal.hasRole(RoleKey.PLATFORM_ADMIN)) {
            return;
        }
        
        // TODO: Query MatterAssignee or similar table to verify the specific lawyer/staff 
        // is authorized to view or edit this matterId.
        // If not assigned, throw FORBIDDEN.
    }
}

package com.lawauto.backend.operations;

import com.lawauto.backend.auth.AuthPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class OperationAccessGuard {

    private final com.lawauto.backend.matter.MatterAssigneeRepository matterAssigneeRepository;

    public OperationAccessGuard(com.lawauto.backend.matter.MatterAssigneeRepository matterAssigneeRepository) {
        this.matterAssigneeRepository = matterAssigneeRepository;
    }

    public void requireMatterAccess(AuthPrincipal principal, UUID matterId) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        
        // Global admins bypass specific matter assignment checks
        if (principal.isPlatformAdmin() || principal.isOrgAdmin()) {
            return;
        }
        
        // Check specific assignment
        boolean hasAccess = matterAssigneeRepository.hasAccess(matterId, principal.userId());
        
        if (!hasAccess) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not assigned to this matter");
        }
    }
}

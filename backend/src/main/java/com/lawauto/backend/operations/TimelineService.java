package com.lawauto.backend.operations;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.operations.dto.MatterTimelineItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TimelineService {

    private final ActivityEventRepository activityEventRepository;
    private final AuthorizationGuard authorizationGuard;
    private final OperationAccessGuard operationAccessGuard;

    public TimelineService(ActivityEventRepository activityEventRepository, 
                           AuthorizationGuard authorizationGuard, 
                           OperationAccessGuard operationAccessGuard) {
        this.activityEventRepository = activityEventRepository;
        this.authorizationGuard = authorizationGuard;
        this.operationAccessGuard = operationAccessGuard;
    }

    /**
     * Retrieves the optimized Timeline Read Model for a specific Matter.
     * Protected by both Tenant boundaries and Data-level access controls.
     */
    @Transactional(readOnly = true)
    public Page<MatterTimelineItem> getMatterTimeline(UUID orgId, UUID matterId, Pageable pageable) {
        // 1. Enforce access control
        authorizationGuard.requireOrg(orgId);
        operationAccessGuard.requireMatterAccess(authorizationGuard.currentPrincipal(), matterId);
        
        // 2. Fetch via optimized Interface Projection
        return activityEventRepository.findByOrgIdAndMatterIdOrderByCreatedAtDesc(orgId, matterId, pageable);
    }
}

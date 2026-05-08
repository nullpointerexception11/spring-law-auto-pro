package com.lawauto.backend.matter;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.operations.OperationAccessGuard;
import com.lawauto.backend.matter.dto.MatterListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MatterService {
    
    private final MatterRepository matterRepository;
    private final AuthorizationGuard authorizationGuard;
    private final OperationAccessGuard operationAccessGuard;

    public MatterService(MatterRepository matterRepository, 
                         AuthorizationGuard authorizationGuard, 
                         OperationAccessGuard operationAccessGuard) {
        this.matterRepository = matterRepository;
        this.authorizationGuard = authorizationGuard;
        this.operationAccessGuard = operationAccessGuard;
    }

    /**
     * Retrieves an optimized, paginated list of matters for the dashboard.
     * Enforces organization-level isolation.
     */
    @Transactional(readOnly = true)
    public Page<MatterListDto> listMatters(UUID orgId, Pageable pageable) {
        // Enforce tenant isolation boundary
        authorizationGuard.requireOrg(orgId);
        
        // TODO: In a fully fleshed out permission model, non-admins would require 
        // a modified repository query that joins the MatterAssignee table.
        // For now, returning all org matters.
        
        return matterRepository.findAllListDtosByOrgId(orgId, pageable);
    }
}

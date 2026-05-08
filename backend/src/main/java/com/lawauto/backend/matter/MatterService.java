package com.lawauto.backend.matter;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.operations.OperationAccessGuard;
import com.lawauto.backend.matter.dto.MatterListDto;
import com.lawauto.backend.matter.dto.MatterDetailDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class MatterService {
    
    private final MatterRepository matterRepository;
    private final MatterPartyRepository matterPartyRepository;
    private final com.lawauto.backend.org.OrgRepository orgRepository;
    private final AuthorizationGuard authorizationGuard;
    private final OperationAccessGuard operationAccessGuard;

    public MatterService(MatterRepository matterRepository, 
                         MatterPartyRepository matterPartyRepository,
                         com.lawauto.backend.org.OrgRepository orgRepository,
                         AuthorizationGuard authorizationGuard, 
                         OperationAccessGuard operationAccessGuard) {
        this.matterRepository = matterRepository;
        this.matterPartyRepository = matterPartyRepository;
        this.orgRepository = orgRepository;
        this.authorizationGuard = authorizationGuard;
        this.operationAccessGuard = operationAccessGuard;
    }

    /**
     * Creates a new Matter record.
     */
    @Transactional
    @SuppressWarnings("null")
    public UUID createMatter(@org.springframework.lang.NonNull UUID orgId, com.lawauto.backend.matter.dto.CreateMatterRequest request) {
        java.util.Objects.requireNonNull(orgId, "orgId must not be null");
        
        // 1. Fetch organization
        com.lawauto.backend.org.Org org = orgRepository.findById(orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found"));

        // 2. Map DTO to Entity
        Matter matter = Matter.builder()
                .org(org)
                .title(request.title())
                .referenceNumber(request.referenceNumber())
                .summary(request.summary())
                .description(request.description())
                .tags(request.tags())
                .openedAt(request.openedAt() != null ? request.openedAt() : java.time.OffsetDateTime.now())
                .status(MatterStatus.ACTIVE)
                .build();

        // 3. Persist
        return matterRepository.save(matter).getId();
    }

    /**
     * Retrieves an optimized, paginated list of matters for the dashboard.
     * Enforces organization-level isolation.
     */
    @Transactional(readOnly = true)
    public Page<MatterListDto> listMatters(UUID orgId, Pageable pageable) {
        authorizationGuard.requireOrg(orgId);
        
        // TODO: Non-admin specific filtering logic
        
        return matterRepository.findAllListDtosByOrgId(orgId, pageable);
    }

    /**
     * Retrieves the highly optimized comprehensive Matter Detail Read Model.
     * Enforces fine-grained data-level access control.
     */
    @Transactional(readOnly = true)
    public MatterDetailDto getMatterDetail(UUID orgId, UUID matterId) {
        // 1. Tenant & Role Authorization
        authorizationGuard.requireOrg(orgId);
        operationAccessGuard.requireMatterAccess(authorizationGuard.currentPrincipal(), matterId);

        // 2. Fetch Core Matter + Litigation Details (Single LEFT JOIN query)
        MatterDetailDto detailDto = matterRepository.findDetailDtoByIdAndOrgId(matterId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matter not found"));

        // 3. Fetch Contextual Parties (Separated query to avoid Cartesian product / N+1)
        List<MatterDetailDto.PartySummaryDto> parties = matterPartyRepository.findPartiesByMatterId(matterId);

        // 4. Assemble and return the immutable Read Model
        return detailDto.withParties(parties);
    }
}

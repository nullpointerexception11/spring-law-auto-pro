package com.lawauto.backend.matter;

import com.lawauto.backend.matter.dto.MatterListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/matters")
@RequiredArgsConstructor
public class MatterController {

    private final MatterService matterService;
    private final com.lawauto.backend.auth.AuthorizationGuard authorizationGuard;

    /**
     * GET /api/matters?page=0&size=20
     * Returns a paginated, optimized list of matters for the requesting organization.
     */
    @GetMapping
    public Page<MatterListDto> listMatters(Pageable pageable) {
        var principal = java.util.Objects.requireNonNull(authorizationGuard.currentPrincipal());
        return matterService.listMatters(java.util.Objects.requireNonNull(principal.orgId()), pageable);
    }

    /**
     * GET /api/matters/{matterId}
     * Returns a highly optimized, comprehensive Read Model for the Matter Detail view.
     */
    @GetMapping("/{matterId}")
    public com.lawauto.backend.matter.dto.MatterDetailDto getMatterDetail(
            @PathVariable @org.springframework.lang.NonNull UUID matterId) {
        var principal = java.util.Objects.requireNonNull(authorizationGuard.currentPrincipal());
        return matterService.getMatterDetail(java.util.Objects.requireNonNull(principal.orgId()), matterId);
    }

    @PostMapping
    public java.util.UUID createMatter(
            @org.springframework.web.bind.annotation.RequestBody com.lawauto.backend.matter.dto.CreateMatterRequest request) {
        var principal = java.util.Objects.requireNonNull(authorizationGuard.currentPrincipal());
        return matterService.createMatter(java.util.Objects.requireNonNull(principal.orgId()), request);
    }
}

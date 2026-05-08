package com.lawauto.backend.petition;

import static com.lawauto.backend.petition.PetitionDraftDtos.*;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import com.lawauto.backend.operations.OperationAccessGuard;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matters/{matterId}/petition-drafts")
public class MatterPetitionDraftController {
    private final AuthorizationGuard authorizationGuard;
    private final OperationAccessGuard operationAccessGuard;
    private final PetitionDraftService service;

    public MatterPetitionDraftController(
            AuthorizationGuard authorizationGuard,
            OperationAccessGuard operationAccessGuard,
            PetitionDraftService service
    ) {
        this.authorizationGuard = authorizationGuard;
        this.operationAccessGuard = operationAccessGuard;
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<PetitionDraftDto>> list(@PathVariable UUID matterId, @RequestParam UUID orgId) {
        authorizationGuard.requireOrg(orgId);
        operationAccessGuard.requireMatterAccess(authorizationGuard.currentPrincipal(), matterId);
        return ApiResponse.ok(service.listByMatter(orgId, matterId));
    }

    @PostMapping
    public Map<String, UUID> create(@PathVariable UUID matterId, @Valid @RequestBody CreatePetitionDraftRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        if (!matterId.equals(req.matterId())) throw new IllegalArgumentException("matterId path/body mismatch");
        operationAccessGuard.requireMatterAccess(authorizationGuard.currentPrincipal(), matterId);
        return Map.of("id", service.create(req));
    }

    @PatchMapping("/{draftId}")
    public ApiResponse<String> update(
            @PathVariable UUID matterId,
            @PathVariable UUID draftId,
            @RequestParam UUID orgId,
            @Valid @RequestBody UpdatePetitionDraftRequest req
    ) {
        authorizationGuard.requireOrg(orgId);
        operationAccessGuard.requireMatterAccess(authorizationGuard.currentPrincipal(), matterId);
        service.update(orgId, matterId, draftId, req);
        return ApiResponse.ok("petition-draft-updated");
    }
}

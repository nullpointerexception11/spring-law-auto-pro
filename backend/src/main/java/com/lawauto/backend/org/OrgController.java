package com.lawauto.backend.org;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orgs")
public class OrgController {
    private final OrgRepository orgRepository;
    private final AuthorizationGuard authorizationGuard;

    public OrgController(OrgRepository orgRepository, AuthorizationGuard authorizationGuard) {
        this.orgRepository = orgRepository;
        this.authorizationGuard = authorizationGuard;
    }

    @GetMapping("/me")
    public ApiResponse<Org> me() {
        AuthPrincipal principal = authorizationGuard.currentPrincipal();
        Org org = orgRepository.findById(principal.orgId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        return ApiResponse.ok(org);
    }
}

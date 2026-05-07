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
    private final OrgService orgService;
    private final AuthorizationGuard authorizationGuard;

    public OrgController(OrgService orgService, AuthorizationGuard authorizationGuard) {
        this.orgService = orgService;
        this.authorizationGuard = authorizationGuard;
    }

    @GetMapping("/me")
    public ApiResponse<OrgResponseDto> me() {
        AuthPrincipal principal = authorizationGuard.currentPrincipal();
        OrgResponseDto orgResponse = orgService.getOrg(principal.orgId());
        return ApiResponse.ok(orgResponse);
    }
}

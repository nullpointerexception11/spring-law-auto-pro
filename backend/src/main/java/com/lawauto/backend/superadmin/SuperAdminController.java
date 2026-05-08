package com.lawauto.backend.superadmin;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/super-admin")
@RequiredArgsConstructor
public class SuperAdminController {

    private final SuperAdminService superAdminService;
    private final AuthorizationGuard authorizationGuard;

    @PostMapping("/organizations")
    public ApiResponse<Map<String, UUID>> createOrg(@Valid @RequestBody CreateOrgRequest request) {
        authorizationGuard.requireRole("SUPER_ADMIN");
        UUID orgId = superAdminService.createOrganization(request);
        return ApiResponse.ok(Map.of("orgId", orgId));
    }
}

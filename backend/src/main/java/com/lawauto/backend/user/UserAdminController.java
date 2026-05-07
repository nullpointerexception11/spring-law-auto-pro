package com.lawauto.backend.user;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import com.lawauto.backend.common.PageMeta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserAdminController {
    private final AuthorizationGuard authorizationGuard;
    private final UserAdminService userAdminService;

    public UserAdminController(AuthorizationGuard authorizationGuard, UserAdminService userAdminService) {
        this.authorizationGuard = authorizationGuard;
        this.userAdminService = userAdminService;
    }

    @GetMapping
    public ApiResponse<List<UserSummary>> list(
            @RequestParam UUID orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        authorizationGuard.requireOrg(orgId);
        authorizationGuard.requireRole("ADMIN");
        List<UserSummary> users = userAdminService.listUsers(orgId, page, size, sort).stream()
                .map(u -> new UserSummary(u.getId(), u.getOrgId(), u.getEmail(), u.getFullName(), u.getStatus(), u.getCreatedAt(), u.getUpdatedAt()))
                .toList();
        long total = userAdminService.countUsers(orgId);
        return ApiResponse.ok(users, new PageMeta(page, size, total, sort));
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserAdminService.UserDetail> detail(
            @RequestParam UUID orgId,
            @PathVariable UUID userId
    ) {
        authorizationGuard.requireOrg(orgId);
        authorizationGuard.requireRole("ADMIN");
        return ApiResponse.ok(userAdminService.getUserDetail(orgId, userId));
    }

    @PatchMapping("/{userId}/role")
    public ApiResponse<String> updateRole(
            @RequestParam UUID orgId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        authorizationGuard.requireOrg(orgId);
        authorizationGuard.requireRole("ADMIN");
        userAdminService.updateUserRole(orgId, userId, request.role());
        return ApiResponse.ok("role-updated");
    }

    @PatchMapping("/{userId}/status")
    public ApiResponse<String> updateStatus(
            @RequestParam UUID orgId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateStatusRequest request
    ) {
        authorizationGuard.requireOrg(orgId);
        authorizationGuard.requireRole("ADMIN");
        userAdminService.updateUserStatus(orgId, userId, request.status());
        return ApiResponse.ok("status-updated");
    }

    public record UpdateRoleRequest(@NotNull RoleKey role) {}
    public record UpdateStatusRequest(@NotBlank String status) {}
    public record UserSummary(
            UUID id,
            UUID orgId,
            String email,
            String fullName,
            String status,
            java.time.LocalDateTime createdAt,
            java.time.LocalDateTime updatedAt
    ) {}
}

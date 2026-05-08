package com.lawauto.backend.user;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import com.lawauto.backend.common.PageMeta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/v1/users")
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
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        authorizationGuard.requireOrg(orgId);
        authorizationGuard.requireRole("ORG_ADMIN", "PLATFORM_ADMIN");
        
        Page<User> usersPage = userAdminService.listUsers(orgId, pageable);
        
        List<UserSummary> users = usersPage.stream()
                .map(u -> new UserSummary(
                        u.getId(), 
                        u.getOrg() != null ? u.getOrg().getId() : null, 
                        u.getEmail(), 
                        u.getFullName(), 
                        u.getStatus(), 
                        u.getCreatedAt(), 
                        u.getUpdatedAt()))
                .toList();
                
        String sortString = pageable.getSort().isSorted() 
                ? pageable.getSort().iterator().next().getProperty() + "," + pageable.getSort().iterator().next().getDirection().name().toLowerCase()
                : "createdAt,desc";

        return ApiResponse.ok(users, new PageMeta(usersPage.getNumber(), usersPage.getSize(), usersPage.getTotalElements(), sortString));
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserAdminService.UserDetail> detail(
            @RequestParam UUID orgId,
            @PathVariable UUID userId
    ) {
        authorizationGuard.requireOrg(orgId);
        authorizationGuard.requireRole("ORG_ADMIN", "PLATFORM_ADMIN");
        return ApiResponse.ok(userAdminService.getUserDetail(orgId, userId));
    }

    @PatchMapping("/{userId}/role")
    public ApiResponse<String> updateRole(
            @RequestParam UUID orgId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        authorizationGuard.requireOrg(orgId);
        authorizationGuard.requireRole("ORG_ADMIN", "PLATFORM_ADMIN");
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
        authorizationGuard.requireRole("ORG_ADMIN", "PLATFORM_ADMIN");
        userAdminService.updateUserStatus(orgId, userId, request.status());
        return ApiResponse.ok("status-updated");
    }

    public record UpdateRoleRequest(@NotNull RoleKey role) {}
    public record UpdateStatusRequest(@NotNull UserStatus status) {}
    public record UserSummary(
            UUID id,
            UUID orgId,
            String email,
            String fullName,
            UserStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}
}

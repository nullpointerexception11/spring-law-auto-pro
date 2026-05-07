package com.lawauto.backend.user;

import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserAdminService {
    private static final Set<String> ALLOWED_USER_STATUSES = Set.of("ACTIVE", "INACTIVE");
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public UserAdminService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public Page<UserEntity> listUsers(UUID orgId, Pageable pageable) {
        return userRepository.findByOrgId(orgId, pageable);
    }

    public UserDetail getUserDetail(UUID orgId, UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!orgId.equals(user.getOrgId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not belong to org");
        }

        RoleKey role = userRoleRepository.findByUserId(userId).stream()
                .findFirst()
                .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()))
                .map(RoleEntity::getKey)
                .orElse(null);

        return new UserDetail(
                user.getId(),
                user.getOrgId(),
                user.getEmail(),
                user.getFullName(),
                user.getStatus(),
                role,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
    public void updateUserRole(UUID orgId, UUID userId, RoleKey roleKey) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!orgId.equals(user.getOrgId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not belong to org");
        }

        RoleEntity role = roleRepository.findByOrgIdAndKey(orgId, roleKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found in org"));

        userRoleRepository.deleteByUserId(userId);
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        userRoleRepository.save(userRole);
    }

    @Transactional
    public void updateUserStatus(UUID orgId, UUID userId, String status) {
        String normalizedStatus = status == null ? null : status.trim().toUpperCase();
        if (normalizedStatus == null || !ALLOWED_USER_STATUSES.contains(normalizedStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported user status");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!orgId.equals(user.getOrgId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not belong to org");
        }

        user.setStatus(normalizedStatus);
        userRepository.save(user);
    }



    public record UserDetail(
            UUID id,
            UUID orgId,
            String email,
            String fullName,
            String status,
            RoleKey role,
            java.time.LocalDateTime createdAt,
            java.time.LocalDateTime updatedAt
    ) {}
}

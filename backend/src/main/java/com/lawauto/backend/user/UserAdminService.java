package com.lawauto.backend.user;

import jakarta.transaction.Transactional;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
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
        log.info("Listing users for org [{}]", orgId);
        return userRepository.findByOrgId(orgId, pageable);
    }

    @Cacheable(value = "userDetails", key = "#userId")
    public UserDetail getUserDetail(UUID orgId, UUID userId) {
        log.info("Getting user detail for user [{}] in org [{}]", userId, orgId);
        UserEntity user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!orgId.equals(user.getOrgId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not belong to org");
        }

        RoleKey role = userRoleRepository.findByUserId(userId).stream()
                .findFirst()
                .flatMap(userRole -> roleRepository.findById(Objects.requireNonNull(userRole.getRoleId())))
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
    @CacheEvict(value = "userDetails", key = "#userId")
    public void updateUserRole(UUID orgId, UUID userId, RoleKey roleKey) {
        log.info("Updating role for user [{}] to [{}] in org [{}]", userId, roleKey, orgId);
        UserEntity user = userRepository.findById(Objects.requireNonNull(userId))
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
    @CacheEvict(value = "userDetails", key = "#userId")
    public void updateUserStatus(UUID orgId, UUID userId, String status) {
        log.info("Updating status for user [{}] to [{}] in org [{}]", userId, status, orgId);
        String normalizedStatus = status == null ? null : status.trim().toUpperCase();
        if (normalizedStatus == null || !ALLOWED_USER_STATUSES.contains(normalizedStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported user status");
        }

        UserEntity user = userRepository.findById(Objects.requireNonNull(userId))
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

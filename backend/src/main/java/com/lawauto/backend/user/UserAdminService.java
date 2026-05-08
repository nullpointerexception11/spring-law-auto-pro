package com.lawauto.backend.user;

import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserAdminService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public Page<User> listUsers(UUID orgId, Pageable pageable) {
        log.info("Listing users for org [{}]", orgId);
        return userRepository.findByOrgId(orgId, pageable);
    }

    @Cacheable(value = "userDetails", key = "#userId")
    public UserDetail getUserDetail(UUID orgId, UUID userId) {
        log.info("Getting user detail for user [{}] in org [{}]", userId, orgId);
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        if (user.getOrg() == null || !orgId.equals(user.getOrg().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not belong to org");
        }

        RoleKey primaryRole = user.getRoles().stream()
                .findFirst()
                .map(Role::getRoleKey)
                .orElse(null);

        return new UserDetail(
                user.getId(),
                user.getOrg().getId(),
                user.getEmail(),
                user.getFullName(),
                user.getStatus(),
                primaryRole,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
    @CacheEvict(value = "userDetails", key = "#userId")
    public void updateUserRole(UUID orgId, UUID userId, RoleKey roleKey) {
        log.info("Updating role for user [{}] to [{}] in org [{}]", userId, roleKey, orgId);
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        if (user.getOrg() == null || !orgId.equals(user.getOrg().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not belong to org");
        }

        Role role = roleRepository.findByOrgIdAndRoleKey(orgId, roleKey)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found in org"));

        user.setRoles(Set.of(role));
        userRepository.save(user);
    }

    @Transactional
    @CacheEvict(value = "userDetails", key = "#userId")
    public void updateUserStatus(UUID orgId, UUID userId, UserStatus newStatus) {
        log.info("Updating status for user [{}] to [{}] in org [{}]", userId, newStatus, orgId);
        User user = userRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        if (user.getOrg() == null || !orgId.equals(user.getOrg().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not belong to org");
        }

        user.setStatus(newStatus);
        userRepository.save(user);
    }

    public record UserDetail(
            UUID id,
            UUID orgId,
            String email,
            String fullName,
            UserStatus status,
            RoleKey role,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {}
}

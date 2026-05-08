package com.lawauto.backend.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByOrgIdAndRoleKey(UUID orgId, RoleKey roleKey);
}

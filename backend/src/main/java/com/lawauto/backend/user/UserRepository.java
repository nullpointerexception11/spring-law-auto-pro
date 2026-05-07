package com.lawauto.backend.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByOrgIdAndEmail(UUID orgId, String email);
    Page<UserEntity> findByOrgId(UUID orgId, Pageable pageable);
}

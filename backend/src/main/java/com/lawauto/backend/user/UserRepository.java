package com.lawauto.backend.user;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByOrgIdAndEmail(UUID orgId, String email);
    List<UserEntity> findByOrgId(UUID orgId, Pageable pageable);
    long countByOrgId(UUID orgId);
}

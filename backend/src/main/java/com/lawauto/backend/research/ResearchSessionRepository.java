package com.lawauto.backend.research;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchSessionRepository extends JpaRepository<ResearchSessionEntity, UUID> {
    List<ResearchSessionEntity> findByOrgIdOrderByCreatedAtDesc(UUID orgId);
    Optional<ResearchSessionEntity> findByIdAndOrgId(UUID id, UUID orgId);
}

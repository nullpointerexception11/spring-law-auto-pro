package com.lawauto.backend.research;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchSessionRepository extends JpaRepository<ResearchSession, UUID> {
    List<ResearchSession> findByOrgIdOrderByCreatedAtDesc(UUID orgId);
    Optional<ResearchSession> findByIdAndOrgId(UUID id, UUID orgId);
}

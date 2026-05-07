package com.lawauto.backend.research;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchResultRepository extends JpaRepository<ResearchResultEntity, UUID> {
    List<ResearchResultEntity> findByResearchSessionIdOrderByCreatedAtDesc(UUID researchSessionId);
}

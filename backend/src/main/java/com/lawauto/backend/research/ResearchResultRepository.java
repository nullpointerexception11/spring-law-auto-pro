package com.lawauto.backend.research;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchResultRepository extends JpaRepository<ResearchResult, UUID> {
    List<ResearchResult> findByResearchSessionIdOrderByCreatedAtDesc(UUID researchSessionId);
}

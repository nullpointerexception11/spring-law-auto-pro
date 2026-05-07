package com.lawauto.backend.research;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResearchNoteRepository extends JpaRepository<ResearchNoteEntity, UUID> {
    List<ResearchNoteEntity> findByResearchSessionIdOrderByCreatedAtDesc(UUID researchSessionId);
}

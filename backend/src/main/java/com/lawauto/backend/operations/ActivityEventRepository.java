package com.lawauto.backend.operations;

import com.lawauto.backend.operations.dto.MatterTimelineItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivityEventRepository extends JpaRepository<ActivityEvent, UUID> {

    /**
     * Highly optimized Timeline query.
     * Uses Spring Data JPA interface projection (MatterTimelineItem) to avoid loading full entities.
     */
    Page<MatterTimelineItem> findByOrgIdAndMatterIdOrderByCreatedAtDesc(UUID orgId, UUID matterId, Pageable pageable);
}

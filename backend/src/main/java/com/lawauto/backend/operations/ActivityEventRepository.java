package com.lawauto.backend.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ActivityEventRepository extends JpaRepository<ActivityEvent, UUID> {
    org.springframework.data.domain.Page<com.lawauto.backend.operations.dto.MatterTimelineItem> findByOrgIdAndMatterIdOrderByCreatedAtDesc(
            java.util.UUID orgId, 
            java.util.UUID matterId, 
            org.springframework.data.domain.Pageable pageable
    );
}

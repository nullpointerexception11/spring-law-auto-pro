package com.lawauto.backend.cases;

import com.lawauto.backend.common.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatterRepository extends JpaRepository<Matter, UUID> {

    // Tenant-First: Every query MUST include orgId for RLS isolation.
    List<Matter> findAllByOrgIdAndRecordStatusOrderByCreatedAtDesc(UUID orgId, RecordStatus status);

    Optional<Matter> findByIdAndOrgId(UUID id, UUID orgId);

    // Convenience method for active matters
    default List<Matter> findActiveMatters(UUID orgId) {
        return findAllByOrgIdAndRecordStatusOrderByCreatedAtDesc(orgId, RecordStatus.ACTIVE);
    }
}

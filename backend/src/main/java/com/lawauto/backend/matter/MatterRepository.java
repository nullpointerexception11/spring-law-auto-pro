package com.lawauto.backend.matter;

import com.lawauto.backend.matter.dto.MatterListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MatterRepository extends JpaRepository<Matter, UUID> {

    /**
     * Highly optimized Read Model query for the Dashboard / List view.
     * Maps directly to the Record to bypass heavy entity hydration.
     */
    @Query("""
        SELECT new com.lawauto.backend.matter.dto.MatterListDto(
            m.id, m.title, m.referenceNumber, m.status, m.openedAt,
            (SELECT p.fullName FROM MatterParty mp JOIN mp.party p JOIN mp.role r WHERE mp.matter = m AND r.category = 'CLIENT' ORDER BY p.createdAt ASC LIMIT 1),
            (SELECT p.fullName FROM MatterParty mp JOIN mp.party p JOIN mp.role r WHERE mp.matter = m AND r.category = 'LAWYER' ORDER BY p.createdAt ASC LIMIT 1),
            (SELECT ue.startAt FROM UniversalEvent ue WHERE ue.matter = m AND ue.type = com.lawauto.backend.operations.UniversalEventType.HEARING AND ue.startAt > CURRENT_TIMESTAMP ORDER BY ue.startAt ASC LIMIT 1)
        )
        FROM Matter m
        WHERE m.org.id = :orgId
        ORDER BY m.openedAt DESC
    """)
    Page<MatterListDto> findAllListDtosByOrgId(@Param("orgId") UUID orgId, Pageable pageable);

    /**
     * Highly optimized comprehensive Read Model query for the Matter Detail view.
     * Uses a LEFT JOIN to fetch LitigationDetail seamlessly without N+1.
     */
    @Query("""
        SELECT new com.lawauto.backend.matter.dto.MatterDetailDto(
            m.id, m.title, m.referenceNumber, m.status, m.summary, m.description, m.tags, m.openedAt, m.closedAt,
            ld.courtName, ld.caseNumber, ld.judgeName, ld.decisionDate
        )
        FROM Matter m
        LEFT JOIN LitigationDetail ld ON m.id = ld.matterId
        WHERE m.id = :matterId AND m.org.id = :orgId
    """)
    java.util.Optional<com.lawauto.backend.matter.dto.MatterDetailDto> findDetailDtoByIdAndOrgId(@Param("matterId") UUID matterId, @Param("orgId") UUID orgId);
}

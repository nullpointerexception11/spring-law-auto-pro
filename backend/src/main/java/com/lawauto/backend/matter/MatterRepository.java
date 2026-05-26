package com.lawauto.backend.matter;

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
    @Query(value = """
        SELECT m.id as id, m.title as title, m.reference_number as referenceNumber,
               COALESCE(m.reference_number, CONCAT('D-', SUBSTRING(CAST(m.id AS text), 1, 8))) as displayId,
               m.status as status, m.opened_at as openedAt,
               (SELECT p.full_name FROM matter_parties mp JOIN parties p ON mp.party_id = p.id JOIN matter_party_roles r ON mp.role_id = r.id WHERE mp.matter_id = m.id AND r.role_key = 'CLIENT' ORDER BY p.created_at ASC LIMIT 1) as clientName,
               (SELECT p.full_name FROM matter_parties mp JOIN parties p ON mp.party_id = p.id JOIN matter_party_roles r ON mp.role_id = r.id WHERE mp.matter_id = m.id AND r.role_key = 'LAWYER' ORDER BY p.created_at ASC LIMIT 1) as assignedLawyerName,
               (SELECT ue.start_at FROM universal_events ue WHERE ue.matter_id = m.id AND ue.type = 'HEARING' AND ue.start_at > CURRENT_TIMESTAMP ORDER BY ue.start_at ASC LIMIT 1) as nextHearingDate,
               m.summary as summary
        FROM matters m
        WHERE m.org_id = :orgId
        ORDER BY m.opened_at DESC
        """, nativeQuery = true)
    Page<com.lawauto.backend.matter.dto.MatterListDtoProjection> findAllListDtosByOrgId(@Param("orgId") UUID orgId, Pageable pageable);

    @Query("""
        SELECT new com.lawauto.backend.matter.dto.MatterDetailDto(
            m.id, m.title, m.referenceNumber, m.status, m.summary, m.description, m.tags, m.openedAt, m.closedAt,
            ld.courtName, ld.caseNumber, ld.judgeName, ld.decisionDate
        )
        FROM Matter m
        LEFT JOIN LitigationDetail ld ON m.id = ld.id
        WHERE m.id = :matterId AND m.org.id = :orgId
    """)
    java.util.Optional<com.lawauto.backend.matter.dto.MatterDetailDto> findDetailDtoByIdAndOrgId(@Param("matterId") UUID matterId, @Param("orgId") UUID orgId);
}

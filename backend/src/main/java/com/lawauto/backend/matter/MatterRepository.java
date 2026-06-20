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
               (SELECT p.full_name FROM matter_party mp JOIN party p ON mp.party_id = p.id JOIN matter_party_role r ON mp.role_id = r.id WHERE mp.matter_id = m.id AND r.name = 'CLIENT' ORDER BY p.created_at ASC LIMIT 1) as clientName,
               (SELECT p.full_name FROM matter_party mp JOIN party p ON mp.party_id = p.id JOIN matter_party_role r ON mp.role_id = r.id WHERE mp.matter_id = m.id AND r.name = 'LAWYER' ORDER BY p.created_at ASC LIMIT 1) as assignedLawyerName,
               (SELECT h.hearing_date FROM hearing h WHERE h.matter_id = m.id AND h.hearing_date > CURRENT_TIMESTAMP ORDER BY h.hearing_date ASC LIMIT 1) as nextHearingDate,
               m.summary as summary
        FROM matter m
        WHERE m.org_id = :orgId
        ORDER BY m.opened_at DESC
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM matter m
        WHERE m.org_id = :orgId
        """,
        nativeQuery = true)
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

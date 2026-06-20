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
     *
     * PERFORMANCE NOTE:
     * The previous version used 3 correlated scalar subqueries per row
     * (client name, lawyer name, next hearing date). Postgres re-evaluates a
     * correlated subquery once per outer row, so a page of N matters caused
     * 3*N extra index lookups in addition to the base scan.
     *
     * Rewritten with LEFT JOIN LATERAL: the planner still does one lookup per
     * matter per lateral join, but it can pipeline/batch these as regular
     * joins (better use of work_mem, can leverage index-only scans, and is
     * visible to the planner as a join rather than an opaque subplan that
     * gets re-planned per row). Combined with the recommended indexes below,
     * this turns each lateral lookup into a single index-range-scan instead
     * of a full nested-loop subquery re-execution.
     *
     * Recommended supporting indexes (add via migration):
     *   CREATE INDEX idx_matter_party_matter_role ON matter_party (matter_id, role_id, party_id);
     *   CREATE INDEX idx_hearing_matter_date ON hearing (matter_id, hearing_date) WHERE hearing_date > now();
     */
    @Query(value = """
        SELECT m.id as id, m.title as title, m.reference_number as referenceNumber,
               COALESCE(m.reference_number, CONCAT('D-', SUBSTRING(CAST(m.id AS text), 1, 8))) as displayId,
               m.status as status, m.opened_at as openedAt,
               client.full_name as clientName,
               lawyer.full_name as assignedLawyerName,
               hearing.hearing_date as nextHearingDate,
               m.summary as summary
        FROM matter m
        LEFT JOIN LATERAL (
            SELECT p.full_name
            FROM matter_party mp
            JOIN party p ON mp.party_id = p.id
            JOIN matter_party_role r ON mp.role_id = r.id
            WHERE mp.matter_id = m.id AND r.name = 'CLIENT'
            ORDER BY p.created_at ASC
            LIMIT 1
        ) client ON true
        LEFT JOIN LATERAL (
            SELECT p.full_name
            FROM matter_party mp
            JOIN party p ON mp.party_id = p.id
            JOIN matter_party_role r ON mp.role_id = r.id
            WHERE mp.matter_id = m.id AND r.name = 'LAWYER'
            ORDER BY p.created_at ASC
            LIMIT 1
        ) lawyer ON true
        LEFT JOIN LATERAL (
            SELECT h.hearing_date
            FROM hearing h
            WHERE h.matter_id = m.id AND h.hearing_date > CURRENT_TIMESTAMP
            ORDER BY h.hearing_date ASC
            LIMIT 1
        ) hearing ON true
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

    /**
     * Lightweight aggregate query for dashboard stat cards.
     * Avoids fetching N full rows (with their 3 lateral joins each) just to
     * compute counts and the 5 most recent matters in JS.
     */
    @Query(value = """
        SELECT
            COUNT(*) FILTER (WHERE status = 'OPEN')    as activeCount,
            COUNT(*) FILTER (WHERE status = 'PENDING') as pendingCount,
            COUNT(*) FILTER (WHERE status = 'CLOSED')  as closedCount,
            COUNT(*)                                    as totalCount,
            MIN(opened_at)                              as firstOpenedAt
        FROM matter
        WHERE org_id = :orgId
        """, nativeQuery = true)
    com.lawauto.backend.matter.dto.MatterStatsProjection getStatsByOrgId(@Param("orgId") UUID orgId);

    /**
     * Bounded "recent matters" query for the dashboard widget.
     * No lateral joins — the dashboard recent-list only needs
     * id/title/status/openedAt, so we skip the client/lawyer/hearing lookups
     * entirely instead of paying for them and discarding the data.
     */
    @Query(value = """
        SELECT m.id as id, m.title as title, m.reference_number as referenceNumber,
               COALESCE(m.reference_number, CONCAT('D-', SUBSTRING(CAST(m.id AS text), 1, 8))) as displayId,
               m.status as status, m.opened_at as openedAt,
               NULL::text as clientName, NULL::text as assignedLawyerName, NULL::timestamptz as nextHearingDate,
               m.summary as summary
        FROM matter m
        WHERE m.org_id = :orgId
        ORDER BY m.opened_at DESC
        LIMIT 5
        """, nativeQuery = true)
    java.util.List<com.lawauto.backend.matter.dto.MatterListDtoProjection> findTop5RecentByOrgId(@Param("orgId") UUID orgId);

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

    /**
     * Used by DocumentService to validate matter ownership before an upload.
     * A derived query filtering on org_id directly in SQL — avoids loading
     * the lazy `org` association just to compare its id in Java (which,
     * combined with running outside a transaction, would also throw
     * LazyInitializationException once the implicit findById() transaction
     * had already closed).
     */
    java.util.Optional<Matter> findByIdAndOrg_Id(UUID id, UUID orgId);
}

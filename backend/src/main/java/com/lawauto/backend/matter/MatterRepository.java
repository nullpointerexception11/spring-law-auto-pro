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
            m.id, m.title, m.referenceNumber, m.status, m.openedAt
        )
        FROM Matter m
        WHERE m.org.id = :orgId
        ORDER BY m.openedAt DESC
    """)
    Page<MatterListDto> findAllListDtosByOrgId(@Param("orgId") UUID orgId, Pageable pageable);
}

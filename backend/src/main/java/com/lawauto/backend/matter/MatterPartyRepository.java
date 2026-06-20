package com.lawauto.backend.matter;

import com.lawauto.backend.matter.dto.MatterDetailDto.PartySummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MatterPartyRepository extends JpaRepository<MatterParty, UUID> {

    /**
     * Highly optimized query to fetch only the necessary Party details and their Contextual Roles 
     * for a specific Matter. Avoids loading full entities.
     */
    @Query("""
        SELECT new com.lawauto.backend.matter.dto.MatterDetailDto$PartySummaryDto(
            p.id, p.fullName, r.name, r.category
        )
        FROM MatterParty mp
        JOIN mp.party p
        JOIN mp.role r
        WHERE mp.matter.id = :matterId
    """)
    List<PartySummaryDto> findPartiesByMatterId(@Param("matterId") UUID matterId);
}

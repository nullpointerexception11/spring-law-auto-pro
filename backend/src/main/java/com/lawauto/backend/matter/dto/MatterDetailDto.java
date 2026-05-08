package com.lawauto.backend.matter.dto;

import com.lawauto.backend.matter.MatterStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Highly optimized comprehensive Read Model for the Matter Detail UX.
 */
public record MatterDetailDto(
    UUID id,
    String title,
    String referenceNumber,
    MatterStatus status,
    String summary,
    String description,
    String[] tags,
    OffsetDateTime openedAt,
    OffsetDateTime closedAt,
    
    // Litigation Details (Flat projection)
    String courtName,
    String caseNumber,
    String judgeName,
    LocalDate decisionDate,
    
    // Nested Read Models for the Detail View
    List<PartySummaryDto> parties
) {
    public record PartySummaryDto(
        UUID partyId,
        String fullName,
        String roleName, // Comes from MatterPartyRole
        String roleCategory
    ) {}
}

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
    java.util.List<String> tags,
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
    /**
     * Secondary constructor for JPQL mapping. 
     * Initializes the parties list as empty. It will be populated by the Service layer.
     */
    public MatterDetailDto(UUID id, String title, String referenceNumber, MatterStatus status, 
                           String summary, String description, java.util.List<String> tags, 
                           OffsetDateTime openedAt, OffsetDateTime closedAt, 
                           String courtName, String caseNumber, String judgeName, LocalDate decisionDate) {
        this(id, title, referenceNumber, status, summary, description, tags, openedAt, closedAt, 
             courtName, caseNumber, judgeName, decisionDate, List.of());
    }

    /**
     * Wither method to immutably add the parties list after fetching.
     */
    public MatterDetailDto withParties(List<PartySummaryDto> newParties) {
        return new MatterDetailDto(id, title, referenceNumber, status, summary, description, tags, 
                                   openedAt, closedAt, courtName, caseNumber, judgeName, decisionDate, newParties);
    }

    public record PartySummaryDto(
        UUID partyId,
        String fullName,
        String roleName, 
        com.lawauto.backend.matter.PartyCategory category
    ) {}
}

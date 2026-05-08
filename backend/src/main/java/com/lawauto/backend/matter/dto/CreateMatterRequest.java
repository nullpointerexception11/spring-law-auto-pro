package com.lawauto.backend.matter.dto;

import java.time.OffsetDateTime;

/**
 * Request DTO for creating a new Matter.
 */
public record CreateMatterRequest(
    String title,
    String referenceNumber,
    String summary,
    String description,
    String[] tags,
    OffsetDateTime openedAt
) {}

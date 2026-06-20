package com.lawauto.backend.matter.dto;

import com.lawauto.backend.matter.MatterStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Lightweight Read Model for the Dashboard / Matter List view.
 */
public record MatterListDto(
    UUID id,
    String title,
    String referenceNumber,
    String displayId,
    MatterStatus status,
    OffsetDateTime openedAt,
    String clientName,
    String assignedLawyerName,
    OffsetDateTime nextHearingDate,
    String summary
) {}

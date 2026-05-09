package com.lawauto.backend.matter.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Interface projection for Native Query results in MatterRepository.
 */
public interface MatterListDtoProjection {
    UUID getId();
    String getTitle();
    String getReferenceNumber();
    String getStatus();
    OffsetDateTime getOpenedAt();
    String getClientName();
    String getAssignedLawyerName();
    OffsetDateTime getNextHearingDate();
}

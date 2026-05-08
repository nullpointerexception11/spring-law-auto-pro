package com.lawauto.backend.operations.dto;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Optimized Read Model (Projection) for the Matter Timeline UX.
 * Avoids loading full User, Org, or Matter entities into memory.
 */
public interface MatterTimelineItem {
    UUID getId();
    String getAction();
    String getSummary();
    OffsetDateTime getCreatedAt();
    
    // Automatically joins and fetches the User's full name
    String getUserFullName();
    
    // Fetches the flexible JSONB metadata
    Map<String, Object> getMetadata();
}

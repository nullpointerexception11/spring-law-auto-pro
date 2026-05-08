package com.lawauto.backend.cases;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class EventRequest {
    private UUID matterId;
    private UniversalEventType type;
    private String title;
    private String descriptionHtml;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
}

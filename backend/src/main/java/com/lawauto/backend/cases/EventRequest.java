package com.lawauto.backend.cases;

import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class EventRequest {
    private UUID matterId;
    private String type; // HEARING, DEADLINE, etc.
    private String title;
    private String descriptionHtml;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
}

package com.lawauto.backend.operations;

import com.lawauto.backend.operations.dto.MatterTimelineItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/matters/{matterId}/timeline")
public class TimelineController {

    private final TimelineService timelineService;

    public TimelineController(TimelineService timelineService) {
        this.timelineService = timelineService;
    }

    /**
     * GET /api/matters/{matterId}/timeline?orgId={orgId}&page=0&size=20
     * Returns the highly optimized, paginated Timeline Read Model.
     */
    @GetMapping
    public Page<MatterTimelineItem> getTimeline(
            @PathVariable UUID matterId,
            @RequestParam UUID orgId,
            Pageable pageable) {
        return timelineService.getMatterTimeline(orgId, matterId, pageable);
    }
}

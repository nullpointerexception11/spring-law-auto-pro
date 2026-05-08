package com.lawauto.backend.cases;

import com.lawauto.backend.audit.ActivityEvent;
import com.lawauto.backend.audit.ActivityEventRepository;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UniversalEventService {

    private final UniversalEventRepository universalEventRepository;
    private final EventReminderRepository eventReminderRepository;
    private final MatterRepository matterRepository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final ActivityEventRepository activityEventRepository;

    /**
     * WORKFLOW: Add a Hearing or Deadline with automatic reminder and timeline logging.
     */
    @Transactional
    public UniversalEvent addEvent(EventRequest request, UUID orgId, UUID userId) {
        UniversalEvent event = UniversalEvent.builder()
                .org(orgRepository.getReferenceById(orgId))
                .matter(matterRepository.getReferenceById(request.getMatterId()))
                .type(request.getType())
                .title(request.getTitle())
                .descriptionHtml(request.getDescriptionHtml())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .createdBy(userRepository.getReferenceById(userId))
                .status(UniversalEventStatus.PENDING)
                .build();

        UniversalEvent savedEvent = universalEventRepository.save(event);

        // Workflow: Create a default 24-hour reminder
        EventReminder reminder = EventReminder.builder()
                .event(savedEvent)
                .remindAt(savedEvent.getStartAt().minusHours(24))
                .isSent(false)
                .build();
        eventReminderRepository.save(reminder);

        // Workflow: Log to Matter timeline
        ActivityEvent log = ActivityEvent.builder()
                .org(savedEvent.getOrg())
                .user(userRepository.getReferenceById(userId))
                .matter(savedEvent.getMatter())
                .action("EVENT_ADDED")
                .entityType("EVENT")
                .entityId(savedEvent.getId())
                .summary("Yeni " + savedEvent.getType() + " eklendi: " + savedEvent.getTitle() + " (" + savedEvent.getStartAt() + ")")
                .build();
        activityEventRepository.save(log);

        return savedEvent;
    }
}

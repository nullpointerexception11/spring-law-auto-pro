package com.lawauto.backend.cases;

import com.lawauto.backend.audit.ActivityEvent;
import com.lawauto.backend.audit.ActivityEventRepository;
import com.lawauto.backend.user.UserRepository;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.common.RecordStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatterService {

    private final MatterRepository matterRepository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final ActivityEventRepository activityEventRepository;

    /**
     * WORKFLOW-FIRST: Initiate a new Matter with all its initial ecosystem.
     * This is the "Phase 1: Solid Core" cornerstone.
     */
    @Transactional
    public Matter initiateMatter(MatterRequest request, UUID orgId, UUID currentUserId) {
        // 1. Create the Matter Core
        Matter matter = Matter.builder()
                .org(orgRepository.getReferenceById(orgId))
                .title(request.getTitle())
                .type(request.getType())
                .referenceNumber(request.getReferenceNumber())
                .descriptionHtml(request.getDescriptionHtml())
                .createdBy(userRepository.getReferenceById(currentUserId))
                .status(MatterStatus.OPEN)
                .recordStatus(RecordStatus.ACTIVE)
                .build();

        Matter savedMatter = matterRepository.save(matter);

        // 2. Log Activity (Use the ActivityEvent import)
        ActivityEvent event = ActivityEvent.builder()
                .org(savedMatter.getOrg())
                .user(userRepository.getReferenceById(currentUserId))
                .matter(savedMatter)
                .action("MATTER_INITIATED")
                .entityType("MATTER")
                .entityId(savedMatter.getId())
                .summary("Yeni mesele başlatıldı: " + savedMatter.getTitle())
                .build();
        activityEventRepository.save(event);

        return savedMatter;
    }
}

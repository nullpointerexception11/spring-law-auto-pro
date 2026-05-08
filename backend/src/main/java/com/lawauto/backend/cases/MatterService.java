package com.lawauto.backend.cases;

import com.lawauto.backend.audit.ActivityEvent;
import com.lawauto.backend.audit.ActivityEventRepository;
import com.lawauto.backend.client.Party;
import com.lawauto.backend.client.PartyRepository;
import com.lawauto.backend.common.RecordStatus;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.storage.FileFolder;
import com.lawauto.backend.storage.FileFolderRepository;
import com.lawauto.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatterService {

    private final MatterRepository matterRepository;
    private final PartyRepository partyRepository;
    private final MatterPartyRepository matterPartyRepository;
    private final MatterAssignmentRepository matterAssignmentRepository;
    private final ActivityEventRepository activityEventRepository;
    private final FileFolderRepository fileFolderRepository;

    /**
     * WORKFLOW-FIRST: Initiate a new Matter with all its initial ecosystem.
     * This is the "Phase 1: Solid Core" cornerstone.
     */
    @Transactional
    public Matter initiateMatter(MatterRequest request, UUID orgId, UUID currentUserId) {
        // 1. Create the Matter Core
        Matter matter = Matter.builder()
                .org(OrgRepository.getReference(orgId))
                .title(request.getTitle())
                .type(request.getType())
                .referenceNumber(request.getReferenceNumber())
                .descriptionHtml(request.getDescriptionHtml())
                .createdBy(UserRepository.getReference(currentUserId))
                .status(MatterStatus.OPEN)
                .recordStatus(RecordStatus.ACTIVE)
                .build();

        Matter savedMatter = matterRepository.save(matter);

        // 2. Attach Primary Parties (Workflow: "Who is this about?")
        if (request.getPrimaryPartyId() != null) {
            MatterParty partyLink = MatterParty.builder()
                    .id(new MatterPartyId(savedMatter.getId(), request.getPrimaryPartyId()))
                    .matter(savedMatter)
                    .party(partyRepository.getReferenceById(request.getPrimaryPartyId()))
                    .role(request.getPrimaryPartyRole())
                    .isPrimary(true)
                    .build();
            matterPartyRepository.save(partyLink);
        }

        // 3. Assign Lead Lawyer (Workflow: "Who is responsible?")
        MatterAssignment assignment = MatterAssignment.builder()
                .id(new MatterAssignmentId(savedMatter.getId(), currentUserId))
                .matter(savedMatter)
                .user(UserRepository.getReference(currentUserId))
                .role(AssignmentRole.LEAD)
                .build();
        matterAssignmentRepository.save(assignment);

        // 4. Create Initial Folder Structure (Workflow: "Where do we put files?")
        FileFolder rootFolder = FileFolder.builder()
                .org(savedMatter.getOrg())
                .name("Dosya Evrakları - " + savedMatter.getTitle())
                .matter(savedMatter)
                .build();
        fileFolderRepository.save(rootFolder);

        // 5. Write to Timeline (Workflow: "Capture the history")
        ActivityEvent event = ActivityEvent.builder()
                .org(savedMatter.getOrg())
                .user(UserRepository.getReference(currentUserId))
                .matter(savedMatter)
                .action("INITIATED")
                .entityType("MATTER")
                .entityId(savedMatter.getId())
                .summary("Yeni mesele başlatıldı: " + savedMatter.getTitle())
                .build();
        activityEventRepository.save(event);

        return savedMatter;
    }
}

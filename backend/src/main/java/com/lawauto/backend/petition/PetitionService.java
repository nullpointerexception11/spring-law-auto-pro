package com.lawauto.backend.petition;

import com.lawauto.backend.audit.ActivityEvent;
import com.lawauto.backend.audit.ActivityEventRepository;
import com.lawauto.backend.cases.MatterRepository;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PetitionService {

    private final PetitionRepository petitionRepository;
    private final MatterRepository matterRepository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final ActivityEventRepository activityEventRepository;

    /**
     * WORKFLOW: Create a new Petition and log it to the Matter timeline.
     */
    @Transactional
    public Petition createPetition(PetitionRequest request, UUID orgId, UUID userId) {
        Petition petition = Petition.builder()
                .org(orgRepository.getReferenceById(orgId))
                .matter(matterRepository.getReferenceById(request.getMatterId()))
                .title(request.getTitle())
                .bodyHtml(request.getBodyHtml())
                .createdBy(userRepository.getReferenceById(userId))
                .build();

        Petition savedPetition = petitionRepository.save(petition);

        // Workflow Log: Update the Matter timeline
        ActivityEvent event = ActivityEvent.builder()
                .org(savedPetition.getOrg())
                .user(userRepository.getReferenceById(userId))
                .matter(savedPetition.getMatter())
                .action("PETITION_CREATED")
                .entityType("PETITION")
                .entityId(savedPetition.getId())
                .summary("Yeni dilekçe oluşturuldu: " + savedPetition.getTitle())
                .build();
        activityEventRepository.save(event);

        return savedPetition;
    }
}

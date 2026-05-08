package com.lawauto.backend.storage;

import com.lawauto.backend.audit.ActivityEvent;
import com.lawauto.backend.audit.ActivityEventRepository;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.UserRepository;
import com.lawauto.backend.cases.Matter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileObjectRepository fileObjectRepository;
    private final AttachmentRepository attachmentRepository;
    private final FileFolderRepository fileFolderRepository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final ActivityEventRepository activityEventRepository;

    /**
     * WORKFLOW: Upload a file and attach it to an entity (Matter, Petition, etc.)
     */
    @Transactional
    public Attachment uploadAndAttach(FileAttachRequest request, UUID orgId, UUID userId) {
        // 1. Create FileObject (Metadata for the actual storage)
        FileObject file = FileObject.builder()
                .org(orgRepository.getReferenceById(orgId))
                .folder(request.getFolderId() != null ? fileFolderRepository.getReferenceById(request.getFolderId()) : null)
                .storageKey(request.getStorageKey())
                .fileName(request.getFileName())
                .mimeType(request.getMimeType())
                .sizeBytes(request.getSizeBytes())
                .createdBy(userRepository.getReferenceById(userId))
                .ocrStatus(OCRStatus.PENDING)
                .build();
        
        FileObject savedFile = fileObjectRepository.save(file);

        // 2. Link as Attachment (Generic Linking with Org Isolation)
        Attachment attachment = Attachment.builder()
                .org(orgRepository.getReferenceById(orgId))
                .file(savedFile)
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .label(request.getLabel())
                .build();
        
        Attachment savedAttachment = attachmentRepository.save(attachment);

        // 3. Workflow Log: Add to Matter Timeline if linked to a Matter
        if ("MATTER".equalsIgnoreCase(request.getEntityType())) {
            ActivityEvent event = ActivityEvent.builder()
                    .org(orgRepository.getReferenceById(orgId))
                    .user(userRepository.getReferenceById(userId))
                    .matter(Matter.builder().id(request.getEntityId()).build())
                    .action("FILE_ATTACHED")
                    .entityType("FILE")
                    .entityId(savedFile.getId())
                    .summary("Yeni dosya eklendi: " + savedFile.getFileName() + " (" + request.getLabel() + ")")
                    .build();
            activityEventRepository.save(event);
        }

        return savedAttachment;
    }
}

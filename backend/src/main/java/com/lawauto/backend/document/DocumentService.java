package com.lawauto.backend.document;

import com.lawauto.backend.matter.MatterRepository;
import com.lawauto.backend.storage.FileObject;
import com.lawauto.backend.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final FileMetadataRepository fileMetadataRepository;
    private final MatterRepository matterRepository;
    private final StorageService storageService;

    @Transactional
    public FileMetadata uploadDocument(UUID orgId, UUID matterId, MultipartFile file) throws IOException {
        log.info("Uploading document {} for matter {}", file.getOriginalFilename(), matterId);

        var matter = matterRepository.findById(matterId)
                .filter(m -> m.getOrg().getId().equals(orgId))
                .orElseThrow(() -> new RuntimeException("Matter not found or access denied"));

        // 1. Upload to storage service
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();
        
        // Use a simple checksum for now or generate one
        String sha256 = "SHA256-PLACEHOLDER-" + UUID.randomUUID(); 

        UUID storageKey = UUID.randomUUID();
        storageService.put(storageKey, file.getInputStream(), contentType);

        // 2. Create metadata
        FileMetadata metadata = FileMetadata.builder()
                .filename(originalFilename)
                .contentType(contentType)
                .size(size)
                .sha256(sha256)
                .storageKey(storageKey)
                .matter(matter)
                .build();
        
        metadata.setOrgId(orgId);

        return fileMetadataRepository.save(metadata);
    }

    public List<FileMetadata> listDocumentsForMatter(UUID orgId, UUID matterId) {
        // This is a simplified check, ideally should be a query filtering by orgId too
        return fileMetadataRepository.findByMatterId(matterId);
    }
}

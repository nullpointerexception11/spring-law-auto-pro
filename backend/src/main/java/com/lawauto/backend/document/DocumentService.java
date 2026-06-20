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

    /**
     * PERFORMANCE NOTE:
     * Previously this entire method (including the storageService.put(...)
     * call) was wrapped in a single @Transactional block. That call performs
     * network I/O against S3/MinIO/disk and can take anywhere from tens of
     * milliseconds to several seconds for large files — for that whole
     * duration a Hikari connection (pool size = 10, see application.yml) sat
     * idle-but-checked-out, doing nothing but waiting. Under concurrent
     * uploads this starves the pool and blocks unrelated requests
     * (dashboard, login, etc.) that need a connection.
     *
     * Fix: do the slow I/O first with NO open transaction, then open a
     * short-lived transaction only for the metadata insert. The lookup of
     * the parent Matter also moves outside the write transaction since it's
     * a read used only for validation.
     */
    public FileMetadata uploadDocument(UUID orgId, UUID matterId, MultipartFile file) throws IOException {
        log.info("Uploading document {} for matter {}", file.getOriginalFilename(), matterId);

        // 1. Validate ownership directly in SQL (WHERE id = ? AND org_id = ?)
        //    instead of loading the full Matter + lazily-fetched Org and
        //    comparing in Java. Also avoids a LazyInitializationException:
        //    now that this method is no longer wrapped in @Transactional,
        //    the Hibernate session from findById() would already be closed
        //    by the time a `.filter(m -> m.getOrg()...)` lambda ran.
        var matter = matterRepository.findByIdAndOrg_Id(matterId, orgId)
                .orElseThrow(() -> new RuntimeException("Matter not found or access denied"));

        // 2. Slow I/O happens with NO database transaction open.
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();
        String sha256 = "SHA256-PLACEHOLDER-" + UUID.randomUUID();
        UUID storageKey = UUID.randomUUID();
        storageService.put(storageKey, file.getInputStream(), contentType);

        // 3. Build metadata and persist. No explicit @Transactional needed
        //    here: JpaRepository.save() is internally @Transactional for a
        //    single entity write, and the connection is only checked out for
        //    the duration of that one INSERT — not for the upload above.
        //    (Note: had we instead written a `private/protected
        //    @Transactional saveMetadata(...)` method and called it from
        //    here, Spring's proxy-based AOP would NOT intercept that
        //    self-invocation and the annotation would silently do nothing —
        //    a classic pitfall worth flagging explicitly.)
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

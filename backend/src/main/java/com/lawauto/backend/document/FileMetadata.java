package com.lawauto.backend.document;

import com.lawauto.backend.common.TenantAware;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Main entity for file metadata.
 */
@Entity
@Table(name = "file_metadata", indexes = {
        @Index(name = "idx_file_org_sha", columnList = "org_id, sha256")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadata extends TenantAware {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 256)
    private String filename;

    @Column(nullable = false, length = 64)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false, length = 64)
    private String sha256;

    @Column(nullable = false, unique = true)
    private UUID storageKey;

    @OneToOne(mappedBy = "fileMetadata", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private OcrDocument ocrDocument;

    @OneToOne(mappedBy = "fileMetadata", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private AiProcessingResult aiResult;
}

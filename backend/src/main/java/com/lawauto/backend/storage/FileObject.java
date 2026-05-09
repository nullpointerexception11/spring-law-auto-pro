package com.lawauto.backend.storage;

import com.lawauto.backend.org.Org;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "files", indexes = {
    @Index(name = "idx_file_org", columnList = "org_id"),
    @Index(name = "idx_file_sha256", columnList = "sha256")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FileObject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "storage_provider", nullable = false)
    private StorageProvider storageProvider = StorageProvider.S3;

    @Column(name = "storage_key", nullable = false)
    private String storageKey;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(length = 64)
    private String sha256;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "ocr_status", nullable = false)
    private OcrStatus ocrStatus = OcrStatus.NONE;

    @Column(name = "extracted_text", columnDefinition = "text")
    private String extractedText;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

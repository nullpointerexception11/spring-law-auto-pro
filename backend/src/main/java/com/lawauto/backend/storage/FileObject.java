package com.lawauto.backend.storage;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"FileObject\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileObject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folderId")
    private FileFolder folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentFileId")
    private FileObject parentFile;

    @Column(nullable = false)
    @Builder.Default
    private Integer versionNo = 1;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isLatest = true;

    @Column(nullable = false)
    @Builder.Default
    private String storageProvider = "LOCAL";

    @Column(nullable = false)
    @Builder.Default
    private String storageKey = "";

    private String bucket;
    private String region;
    private String encryptionKeyId;

    @Column(nullable = false)
    private String fileName;

    private String mimeType;
    private Long sizeBytes;
    private String sha256;

    @Column(nullable = false)
    @Builder.Default
    private String virusScanStatus = "PENDING";

    private OffsetDateTime virusScannedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OCRStatus ocrStatus = OCRStatus.PENDING;

    private String ocrLanguage;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdByUserId", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

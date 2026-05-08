package com.lawauto.backend.storage;

import com.lawauto.backend.org.Org;
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

    @Builder.Default
    private String storageProvider = "S3";

    @Column(nullable = false)
    private String storageKey;

    @Column(nullable = false)
    private String fileName;

    private String mimeType;

    private Long sizeBytes;

    private String sha256;

    @Builder.Default
    private String ocrStatus = "PENDING";

    @Column(columnDefinition = "text")
    private String extractedText;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

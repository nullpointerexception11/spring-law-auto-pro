package com.lawauto.backend.document;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * Stores the result of an OCR (Optical Character Recognition) operation.
 */
@Entity
@Table(name = "ocr_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_metadata_id", nullable = false, unique = true)
    private FileMetadata fileMetadata;

    @Lob
    @Column(nullable = false, columnDefinition = "text")
    private String extractedText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OcrStatus status;

    private OffsetDateTime finishedAt;
}

enum OcrStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    FAILED
}

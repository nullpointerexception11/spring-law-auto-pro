package com.lawauto.backend.document;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * A generic AI processing result (e.g., summarisation, classification) for a stored file.
 */
@Entity
@Table(name = "ai_processing_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiProcessingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_metadata_id", nullable = false, unique = true)
    private FileMetadata fileMetadata;

    /** Name of the AI model / processor that produced this result */
    @Column(nullable = false, length = 64)
    private String processor;

    /** JSON payload containing structured AI output */
    @Column(columnDefinition = "jsonb", nullable = false)
    private String payload;

    /** When the AI processing completed */
    private OffsetDateTime processedAt;
}

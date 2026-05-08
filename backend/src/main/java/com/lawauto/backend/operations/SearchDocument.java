package com.lawauto.backend.operations;

import com.lawauto.backend.org.Org;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"SearchDocument\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @Column(nullable = false)
    private String entityType; // e.g., 'MATTER', 'PARTY', 'CORRESPONDENCE', 'FILE_OCR'

    @Column(nullable = false)
    private UUID entityId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String body;

    // Note: PostgreSQL 'tsvector' column will be utilized via native queries 
    // in the repository layer for blazing fast Full-Text Search.

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}

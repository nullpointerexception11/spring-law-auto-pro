package com.lawauto.backend.ai.rag;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "legal_chunk", indexes = {
    @Index(name = "idx_legal_chunk_source_type", columnList = "sourceType"),
    @Index(name = "idx_legal_chunk_org_id", columnList = "orgId")
})
public class LegalChunk {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID orgId;

    @Column(nullable = false, length = 50)
    private String sourceType;

    @Column(nullable = false)
    private String sourceName;

    @Column
    private String sourceReference;

    @Column
    private UUID sourceDocumentId;

    @Column(nullable = false)
    private int chunkIndex;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "vector(1536)")
    private float[] embedding;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    public LegalChunk() {}

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (updatedAt == null) updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final LegalChunk chunk = new LegalChunk();

        public Builder id(UUID id) { chunk.id = id; return this; }
        public Builder orgId(UUID orgId) { chunk.orgId = orgId; return this; }
        public Builder sourceType(String sourceType) { chunk.sourceType = sourceType; return this; }
        public Builder sourceName(String sourceName) { chunk.sourceName = sourceName; return this; }
        public Builder sourceReference(String sourceReference) { chunk.sourceReference = sourceReference; return this; }
        public Builder sourceDocumentId(UUID sourceDocumentId) { chunk.sourceDocumentId = sourceDocumentId; return this; }
        public Builder chunkIndex(int chunkIndex) { chunk.chunkIndex = chunkIndex; return this; }
        public Builder content(String content) { chunk.content = content; return this; }
        public Builder embedding(float[] embedding) { chunk.embedding = embedding; return this; }
        public Builder metadata(String metadata) { chunk.metadata = metadata; return this; }
        public LegalChunk build() { return chunk; }
    }

    public UUID getId() { return id; }
    public UUID getOrgId() { return orgId; }
    public String getSourceType() { return sourceType; }
    public String getSourceName() { return sourceName; }
    public String getSourceReference() { return sourceReference; }
    public UUID getSourceDocumentId() { return sourceDocumentId; }
    public int getChunkIndex() { return chunkIndex; }
    public String getContent() { return content; }
    public float[] getEmbedding() { return embedding; }
    public String getMetadata() { return metadata; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setEmbedding(float[] embedding) { this.embedding = embedding; }
    public void setContent(String content) { this.content = content; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}

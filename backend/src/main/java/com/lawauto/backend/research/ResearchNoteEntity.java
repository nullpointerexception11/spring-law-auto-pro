package com.lawauto.backend.research;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"ResearchNote\"")
public class ResearchNoteEntity {
    @Id
    private UUID id;
    @Column(name = "researchSessionId", nullable = false)
    private UUID researchSessionId;
    @Column(name = "userId", nullable = false)
    private UUID userId;
    @Column(name = "noteText", nullable = false)
    private String noteText;
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getResearchSessionId() { return researchSessionId; }
    public void setResearchSessionId(UUID researchSessionId) { this.researchSessionId = researchSessionId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

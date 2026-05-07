package com.lawauto.backend.research;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"ResearchSession\"")
public class ResearchSessionEntity {
    @Id
    private UUID id;
    @Column(name = "orgId", nullable = false)
    private UUID orgId;
    @Column(name = "createdByUserId", nullable = false)
    private UUID createdByUserId;
    @Column(nullable = false)
    private String title;
    private String topic;
    private String notes;
    @Column(name = "scopeType", nullable = false)
    private String scopeType;
    @Column(nullable = false)
    private String status;
    @Column(name = "caseId")
    private UUID caseId;
    @Column(name = "petitionId")
    private UUID petitionId;
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOrgId() { return orgId; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public void setCreatedByUserId(UUID createdByUserId) { this.createdByUserId = createdByUserId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getScopeType() { return scopeType; }
    public void setScopeType(String scopeType) { this.scopeType = scopeType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public UUID getCaseId() { return caseId; }
    public void setCaseId(UUID caseId) { this.caseId = caseId; }
    public UUID getPetitionId() { return petitionId; }
    public void setPetitionId(UUID petitionId) { this.petitionId = petitionId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

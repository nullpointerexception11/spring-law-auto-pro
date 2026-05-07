package com.lawauto.backend.research;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ResearchResult")
public class ResearchResultEntity {
    @Id
    private UUID id;
    @Column(name = "researchSessionId", nullable = false)
    private UUID researchSessionId;
    @Column(nullable = false)
    private String sourceType;
    @Column(nullable = false)
    private String title;
    @Column(name = "decisionDate")
    private LocalDateTime decisionDate;
    @Column(name = "referenceNo")
    private String referenceNo;
    private String url;
    private String snippet;
    @Column(name = "relevanceScore")
    private BigDecimal relevanceScore;
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getResearchSessionId() { return researchSessionId; }
    public void setResearchSessionId(UUID researchSessionId) { this.researchSessionId = researchSessionId; }
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDateTime getDecisionDate() { return decisionDate; }
    public void setDecisionDate(LocalDateTime decisionDate) { this.decisionDate = decisionDate; }
    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }
    public BigDecimal getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(BigDecimal relevanceScore) { this.relevanceScore = relevanceScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

package com.lawauto.backend.research;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ResearchResult")
@Getter
@Setter
@NoArgsConstructor
public class ResearchResultEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID researchSessionId;
    @Column(nullable = false)
    private String sourceType;
    @Column(nullable = false)
    private String title;
    private LocalDateTime decisionDate;
    private String referenceNo;
    private String url;
    private String snippet;
    private BigDecimal relevanceScore;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}

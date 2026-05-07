package com.lawauto.backend.research;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ResearchSession")
@Getter
@Setter
@NoArgsConstructor
public class ResearchSessionEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID orgId;
    @Column(nullable = false)
    private UUID createdByUserId;
    @Column(nullable = false)
    private String title;
    private String topic;
    private String notes;
    @Column(nullable = false)
    private String scopeType;
    @Column(nullable = false)
    private String status;
    private UUID caseId;
    private UUID petitionId;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

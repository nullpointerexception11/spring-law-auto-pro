package com.lawauto.backend.cases;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Case")
@Getter
@Setter
@NoArgsConstructor
public class CaseEntity {
    @Id
    private UUID id;

    @Column(name = "orgId", nullable = false)
    private UUID orgId;

    @Column(name = "clientId", nullable = false)
    private UUID clientId;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status;

    @Column(name = "case_number")
    private String caseNumber;

    @Column(name = "case_type")
    private String caseType;

    @Column(name = "court_name")
    private String courtName;

    @Column(name = "is_insurance")
    private boolean insurance;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "status_court")
    private String statusCourt;

    @Column(name = "status_deadline")
    private LocalDateTime statusDeadline;

    @Column(name = "trial_date")
    private LocalDateTime trialDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}


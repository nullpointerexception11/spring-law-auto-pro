package com.lawauto.backend.cases;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"Case\"")
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

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    public UUID getId() { return id; }
    public UUID getOrgId() { return orgId; }
    public UUID getClientId() { return clientId; }
    public String getTitle() { return title; }
    public CaseStatus getStatus() { return status; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
}


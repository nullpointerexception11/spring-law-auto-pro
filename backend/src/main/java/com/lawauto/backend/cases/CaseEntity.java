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

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}


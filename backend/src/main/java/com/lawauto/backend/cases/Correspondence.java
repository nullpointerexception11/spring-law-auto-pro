package com.lawauto.backend.cases;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"Correspondence\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Correspondence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matterId", nullable = false)
    private Matter matter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorrDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorrType type;

    @Column(nullable = false)
    private LocalDate date;

    private String referenceNo;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registeredByUserId", nullable = false)
    private User registeredBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

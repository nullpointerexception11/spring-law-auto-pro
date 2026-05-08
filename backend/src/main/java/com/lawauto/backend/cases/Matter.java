package com.lawauto.backend.cases;

import com.lawauto.backend.common.RecordStatus;
import com.lawauto.backend.org.Org;
import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"Matter\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MatterType type = MatterType.LITIGATION;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MatterStatus status = MatterStatus.OPEN;

    private String referenceNumber; // Case number, execution number, etc.

    @Column(columnDefinition = "TEXT")
    private String descriptionHtml;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime openedAt;

    private OffsetDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdByUserId", nullable = false)
    private User createdBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RecordStatus recordStatus = RecordStatus.ACTIVE;
}

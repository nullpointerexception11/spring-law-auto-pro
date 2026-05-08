package com.lawauto.backend.operations;

import com.lawauto.backend.matter.Matter;
import com.lawauto.backend.org.Org;
import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"UniversalEvent\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniversalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matterId")
    private Matter matter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UniversalEventType type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String descriptionHtml;

    @Column(nullable = false)
    private OffsetDateTime startAt;

    private OffsetDateTime endAt;

    private String rrule; // iCal recurrence standard

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UniversalEventStatus status = UniversalEventStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdByUserId", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}

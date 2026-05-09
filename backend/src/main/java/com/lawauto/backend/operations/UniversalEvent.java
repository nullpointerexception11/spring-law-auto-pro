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
@Table(name = "universal_events", indexes = {
    @Index(name = "idx_event_org_start", columnList = "org_id, start_at"),
    @Index(name = "idx_event_matter", columnList = "matter_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UniversalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matter_id")
    private Matter matter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UniversalEventType type;

    @Column(nullable = false)
    private String title;

    @Column(name = "description_html", columnDefinition = "text")
    private String descriptionHtml;

    private String location;

    @Column(name = "start_at", nullable = false)
    private OffsetDateTime startAt;

    @Column(name = "end_at")
    private OffsetDateTime endAt;

    private String rrule; // iCal recurrence standard

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private UniversalEventStatus status = UniversalEventStatus.SCHEDULED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}

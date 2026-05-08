package com.lawauto.backend.cases;

import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"MatterStatusHistory\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatterStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matterId", nullable = false)
    private Matter matter;

    @Enumerated(EnumType.STRING)
    private MatterStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatterStatus toStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changedByUserId", nullable = false)
    private User changedBy;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime changedAt;
}

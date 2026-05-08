package com.lawauto.backend.operations;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"DeleteRequest\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private UUID entityId;

    @Column(nullable = false)
    private String mode; // SOFT, HARD

    @Column(nullable = false)
    private String status; // PENDING, APPROVED, etc.

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(columnDefinition = "TEXT")
    private String executionError;

    @Column(columnDefinition = "jsonb")
    private String rollbackInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestedByUserId", nullable = false)
    private User requestedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewedByUserId")
    private User reviewedBy;

    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executedByUserId")
    private User executedBy;

    private LocalDateTime executedAt;
}

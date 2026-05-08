package com.lawauto.backend.audit;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"AiInteraction\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Column(columnDefinition = "TEXT")
    private String response;

    private Integer tokenCount;

    @Column(nullable = false)
    @Builder.Default
    private Boolean containsSensitiveData = false;

    private OffsetDateTime retentionExpiresAt;

    private String relatedEntityType;
    private UUID relatedEntityId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

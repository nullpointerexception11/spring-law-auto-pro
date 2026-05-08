package com.lawauto.backend.operations;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"ApiKey\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdByUserId", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String keyHash;

    @Column(nullable = false)
    private String prefix;

    @ElementCollection
    @CollectionTable(name = "ApiKeyScopes", joinColumns = @JoinColumn(name = "apiKeyId"))
    @Column(name = "scope")
    private List<String> scopes;

    private OffsetDateTime lastUsedAt;
    private OffsetDateTime expiresAt;
    private OffsetDateTime revokedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

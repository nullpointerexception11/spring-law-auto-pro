package com.lawauto.backend.user;

import com.lawauto.backend.org.Org;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "\"User\"", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"orgId", "email"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

    @Builder.Default
    private String timezone = "Europe/Istanbul";

    @Builder.Default
    private String locale = "tr-TR";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> notificationSettings;

    private OffsetDateTime lastLoginAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "\"UserRole\"",
        joinColumns = @JoinColumn(name = "userId"),
        inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private Set<Role> roles;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}

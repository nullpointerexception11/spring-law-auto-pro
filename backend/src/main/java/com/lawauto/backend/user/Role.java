package com.lawauto.backend.user;

import com.lawauto.backend.org.Org;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "role", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "role_key"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_key", nullable = false)
    private RoleKey roleKey;

    @Column(nullable = false)
    private String displayName;

    @Builder.Default
    @Column(nullable = false)
    private boolean systemRole = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

package com.lawauto.backend.user;

import com.lawauto.backend.org.Org;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"Role\"", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"orgId", "roleKey"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleKey roleKey;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

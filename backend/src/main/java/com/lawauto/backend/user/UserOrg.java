package com.lawauto.backend.user;

import com.lawauto.backend.org.Org;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "\"UserOrg\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrg {

    @EmbeddedId
    private UserOrgId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orgId")
    @JoinColumn(name = "orgId")
    private Org org;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isOwner = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime joinedAt;
}

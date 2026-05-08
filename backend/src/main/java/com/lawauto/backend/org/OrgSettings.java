package com.lawauto.backend.org;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"OrgSettings\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgSettings {

    @Id
    private UUID orgId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "orgId")
    private Org org;

    @Column(nullable = false)
    @Builder.Default
    private Boolean requireSecretary = false;

    @Column(nullable = false)
    @Builder.Default
    private String secretaryMode = "BASIC";

    @Column(nullable = false)
    @Builder.Default
    private Integer maxUsers = 5;

    @Column(nullable = false)
    @Builder.Default
    private Long maxStorageBytes = 1073741824L; // 1GB

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

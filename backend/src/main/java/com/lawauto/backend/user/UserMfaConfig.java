package com.lawauto.backend.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"UserMfaConfig\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMfaConfig {

    @Id
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "userId")
    private User user;

    private String totpSecret;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isEnabled = false;

    @ElementCollection
    @CollectionTable(name = "UserMfaBackupCodes", joinColumns = @JoinColumn(name = "userId"))
    @Column(name = "backupCodeHash")
    private List<String> backupCodes;

    private OffsetDateTime enabledAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}

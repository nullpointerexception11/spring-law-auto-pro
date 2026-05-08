package com.lawauto.backend.cases;

import com.lawauto.backend.client.Client;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"CaseClient\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseClient {

    @EmbeddedId
    private CaseClientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("caseId")
    @JoinColumn(name = "caseId")
    private CaseEntity caseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clientId")
    @JoinColumn(name = "clientId")
    private Client client;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;
}

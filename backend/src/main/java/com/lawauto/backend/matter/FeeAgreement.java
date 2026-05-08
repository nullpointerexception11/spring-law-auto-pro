package com.lawauto.backend.matter;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.storage.FileObject;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"FeeAgreement\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matterId", nullable = false, unique = true)
    private Matter matter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType;

    @Column(precision = 19, scale = 4)
    private BigDecimal baseAmount;

    @Column(precision = 5, scale = 2)
    private BigDecimal successPercentage;

    @Builder.Default
    private String currency = "TRY";

    private OffsetDateTime signedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signedFileId")
    private FileObject signedFile;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

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
@Table(name = "fee_agreements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FeeAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Org org;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matter_id", nullable = false, unique = true)
    private Matter matter;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_type", nullable = false)
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private FeeAgreementStatus status = FeeAgreementStatus.DRAFT;

    @Column(name = "base_amount", precision = 19, scale = 4)
    private BigDecimal baseAmount;

    @Column(name = "success_percentage", precision = 5, scale = 2)
    private BigDecimal successPercentage;

    @Column(name = "success_basis")
    private String successBasis; // e.g., "COLLECTED_AMOUNT", "AWARDED_AMOUNT"

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Currency currency = Currency.TRY;

    @Column(name = "signed_at")
    private OffsetDateTime signedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signed_file_id")
    private FileObject signedFile;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

package com.lawauto.backend.finance;

import com.lawauto.backend.cases.Matter;
import com.lawauto.backend.org.Org;
import com.lawauto.backend.storage.FileObject;
import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"MatterPayment\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatterPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matterId", nullable = false)
    private Matter matter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoiceId")
    private Invoice invoice;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Builder.Default
    private String currency = "TRY";

    @Column(nullable = false)
    @Builder.Default
    private OffsetDateTime paidAt = OffsetDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentMethod method = PaymentMethod.BANK_TRANSFER;

    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiptFileId")
    private FileObject receiptFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recordedByUserId", nullable = false)
    private User recordedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    private OffsetDateTime deletedAt;
}

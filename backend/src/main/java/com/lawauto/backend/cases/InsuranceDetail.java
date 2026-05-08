package com.lawauto.backend.cases;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"InsuranceDetail\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caseId", nullable = false, unique = true)
    private CaseEntity caseEntity;

    private String crashProvince;
    private String carMark;
    private String carModel;
    private String carPlate;
    private Integer carKm;
    private BigDecimal carPrice;
    private BigDecimal damageAmount;
    private String partReplacement;
    private String partRepaired;
    private String defectRate;
    private String opponentName;
    private String opponentIdCardNo;
    private String opponentPlate;
    private String insuranceCompany;
    private String policyNo;
    private LocalDate policyStart;
    private LocalDate policyEnd;
    private String arbitrationSubject;
    private BigDecimal disputeAmount;

    @Column(columnDefinition = "TEXT")
    private String specialNotes;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

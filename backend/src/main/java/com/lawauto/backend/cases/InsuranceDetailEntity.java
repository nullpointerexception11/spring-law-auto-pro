package com.lawauto.backend.cases;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "InsuranceDetail")
@Getter
@Setter
@NoArgsConstructor
public class InsuranceDetailEntity {
    @Id
    private UUID id;

    @Column(name = "case_id", nullable = false, unique = true)
    private UUID caseId;

    @Column(name = "crash_province")
    private String crashProvince;

    @Column(name = "car_mark")
    private String carMark;

    @Column(name = "car_model")
    private String carModel;

    @Column(name = "car_plate")
    private String carPlate;

    @Column(name = "car_km")
    private Integer carKm;

    @Column(name = "car_price")
    private BigDecimal carPrice;

    @Column(name = "damage_amount")
    private BigDecimal damageAmount;

    @Column(name = "part_replacement", columnDefinition = "TEXT")
    private String partReplacement;

    @Column(name = "part_repaired", columnDefinition = "TEXT")
    private String partRepaired;

    @Column(name = "defect_rate")
    private String defectRate;

    @Column(name = "opponent_name")
    private String opponentName;

    @Column(name = "opponent_id_card_no")
    private String opponentIdCardNo;

    @Column(name = "opponent_plate")
    private String opponentPlate;

    @Column(name = "insurance_company")
    private String insuranceCompany;

    @Column(name = "policy_no")
    private String policyNo;

    @Column(name = "policy_start")
    private LocalDate policyStart;

    @Column(name = "policy_end")
    private LocalDate policyEnd;

    @Column(name = "arbitration_subject", columnDefinition = "TEXT")
    private String arbitrationSubject;

    @Column(name = "dispute_amount")
    private BigDecimal disputeAmount;

    @Column(name = "special_notes", columnDefinition = "TEXT")
    private String specialNotes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

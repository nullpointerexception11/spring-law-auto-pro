package com.lawauto.backend.cases;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class CaseDtos {

    public record CreateCaseRequest(
            UUID orgId,
            UUID clientId,
            String title,
            String caseNumber,
            String caseType,
            String courtName,
            boolean insurance,
            String notes,
            String statusCourt,
            LocalDateTime statusDeadline,
            LocalDateTime trialDate,
            InsuranceDetailRequest insuranceDetail
    ) {}

    public record InsuranceDetailRequest(
            String crashProvince,
            String carMark,
            String carModel,
            String carPlate,
            Integer carKm,
            BigDecimal carPrice,
            BigDecimal damageAmount,
            String partReplacement,
            String partRepaired,
            String defectRate,
            String opponentName,
            String opponentIdCardNo,
            String opponentPlate,
            String insuranceCompany,
            String policyNo,
            LocalDate policyStart,
            LocalDate policyEnd,
            String arbitrationSubject,
            BigDecimal disputeAmount,
            String specialNotes
    ) {}

    public record CaseResponse(
            UUID id,
            String title,
            String caseNumber,
            String caseType,
            CaseStatus status,
            boolean insurance,
            LocalDateTime createdAt,
            InsuranceDetailResponse insuranceDetail
    ) {}

    public record InsuranceDetailResponse(
            String crashProvince,
            String carMark,
            String carModel,
            String carPlate,
            Integer carKm,
            BigDecimal carPrice,
            BigDecimal damageAmount,
            String partReplacement,
            String partRepaired,
            String defectRate,
            String opponentName,
            String opponentIdCardNo,
            String opponentPlate,
            String insuranceCompany,
            String policyNo,
            LocalDate policyStart,
            LocalDate policyEnd,
            String arbitrationSubject,
            BigDecimal disputeAmount,
            String specialNotes
    ) {}
}

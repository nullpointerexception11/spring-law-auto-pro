package com.lawauto.backend.cases;

import com.lawauto.backend.auth.AuthPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CaseService {

    private final CaseRepository caseRepository;
    private final InsuranceDetailRepository insuranceDetailRepository;

    public CaseService(CaseRepository caseRepository, InsuranceDetailRepository insuranceDetailRepository) {
        this.caseRepository = caseRepository;
        this.insuranceDetailRepository = insuranceDetailRepository;
    }

    public Page<CaseResponseDto> listCases(UUID orgId, AuthPrincipal principal, Pageable pageable) {
        log.info("Listing cases for org [{}] with role [{}], user [{}]", orgId, principal.role(), principal.userId());
        Page<CaseEntity> casesPage;

        switch (principal.role()) {
            case "ADMIN":
                throw new AccessDeniedException("Forbidden: Administrative role cannot access legal cases");
            case "LAWYER":
                casesPage = caseRepository.findVisibleForLawyer(orgId, principal.userId(), pageable);
                break;
            case "SECRETARY":
                casesPage = caseRepository.findVisibleForSecretary(orgId, principal.userId(), pageable);
                break;
            default:
                throw new AccessDeniedException("Forbidden: unsupported role");
        }

        return casesPage.map(CaseResponseDto::fromEntity);
    }

    @Transactional
    public UUID createCase(CaseDtos.CreateCaseRequest req) {
        log.info("Creating case [{}] for org [{}]", req.title(), req.orgId());
        
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setId(UUID.randomUUID());
        caseEntity.setOrgId(req.orgId());
        caseEntity.setClientId(req.clientId());
        caseEntity.setTitle(req.title());
        caseEntity.setCaseNumber(req.caseNumber());
        caseEntity.setCaseType(req.caseType());
        caseEntity.setCourtName(req.courtName());
        caseEntity.setInsurance(req.insurance());
        caseEntity.setNotes(req.notes());
        caseEntity.setStatusCourt(req.statusCourt());
        caseEntity.setStatusDeadline(req.statusDeadline());
        caseEntity.setTrialDate(req.trialDate());
        caseEntity.setStatus(CaseStatus.OPEN); // Default to OPEN

        caseRepository.save(caseEntity);

        if (req.insurance() && req.insuranceDetail() != null) {
            log.info("Saving insurance details for case [{}]", caseEntity.getId());
            InsuranceDetailEntity insuranceEntity = new InsuranceDetailEntity();
            insuranceEntity.setId(UUID.randomUUID());
            insuranceEntity.setCaseId(caseEntity.getId());
            
            CaseDtos.InsuranceDetailRequest detail = req.insuranceDetail();
            insuranceEntity.setCrashProvince(detail.crashProvince());
            insuranceEntity.setCarMark(detail.carMark());
            insuranceEntity.setCarModel(detail.carModel());
            insuranceEntity.setCarPlate(detail.carPlate());
            insuranceEntity.setCarKm(detail.carKm());
            insuranceEntity.setCarPrice(detail.carPrice());
            insuranceEntity.setDamageAmount(detail.damageAmount());
            insuranceEntity.setPartReplacement(detail.partReplacement());
            insuranceEntity.setPartRepaired(detail.partRepaired());
            insuranceEntity.setDefectRate(detail.defectRate());
            insuranceEntity.setOpponentName(detail.opponentName());
            insuranceEntity.setOpponentIdCardNo(detail.opponentIdCardNo());
            insuranceEntity.setOpponentPlate(detail.opponentPlate());
            insuranceEntity.setInsuranceCompany(detail.insuranceCompany());
            insuranceEntity.setPolicyNo(detail.policyNo());
            insuranceEntity.setPolicyStart(detail.policyStart());
            insuranceEntity.setPolicyEnd(detail.policyEnd());
            insuranceEntity.setArbitrationSubject(detail.arbitrationSubject());
            insuranceEntity.setDisputeAmount(detail.disputeAmount());
            insuranceEntity.setSpecialNotes(detail.specialNotes());

            insuranceDetailRepository.save(insuranceEntity);
        }

        return caseEntity.getId();
    }
}

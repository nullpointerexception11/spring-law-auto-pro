package com.lawauto.backend.petition;

import com.lawauto.backend.cases.InsuranceDetailRepository;
import com.lawauto.backend.cases.CaseRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PetitionPlaceholderService {

    private final CaseRepository caseRepository;
    private final InsuranceDetailRepository insuranceRepository;

    public PetitionPlaceholderService(CaseRepository caseRepository, InsuranceDetailRepository insuranceRepository) {
        this.caseRepository = caseRepository;
        this.insuranceRepository = insuranceRepository;
    }

    @Cacheable(value = "petitionPlaceholders", key = "#caseId")
    public Map<String, String> getPlaceholders(UUID caseId) {
        java.util.Objects.requireNonNull(caseId);
        log.info("Fetching placeholders for case [{}]", caseId);
        Map<String, String> placeholders = new HashMap<>();
        
        caseRepository.findById(caseId).ifPresent(caseEntity -> {
            placeholders.put("case_title", caseEntity.getTitle());
            placeholders.put("case_number", caseEntity.getCaseNumber());
            placeholders.put("court_name", caseEntity.getCourtName());
            placeholders.put("case_type", caseEntity.getCaseType());
            placeholders.put("status", caseEntity.getStatus().name());
        });

        insuranceRepository.findByCaseId(caseId).ifPresent(ins -> {
            placeholders.put("crash_province", ins.getCrashProvince());
            placeholders.put("car_plate", ins.getCarPlate());
            placeholders.put("car_mark", ins.getCarMark());
            placeholders.put("car_model", ins.getCarModel());
            placeholders.put("car_km", String.valueOf(ins.getCarKm()));
            placeholders.put("car_price", ins.getCarPrice() != null ? ins.getCarPrice().toString() : "");
            placeholders.put("damage_amount", ins.getDamageAmount() != null ? ins.getDamageAmount().toString() : "");
            placeholders.put("opponent_name", ins.getOpponentName());
            placeholders.put("opponent_plate", ins.getOpponentPlate());
            placeholders.put("insurance_company", ins.getInsuranceCompany());
            placeholders.put("policy_no", ins.getPolicyNo());
            placeholders.put("policy_start", ins.getPolicyStart() != null ? ins.getPolicyStart().toString() : "");
            placeholders.put("policy_end", ins.getPolicyEnd() != null ? ins.getPolicyEnd().toString() : "");
            placeholders.put("arbitration_subject", ins.getArbitrationSubject());
            placeholders.put("dispute_amount", ins.getDisputeAmount() != null ? ins.getDisputeAmount().toString() : "");
        });

        return placeholders;
    }

    public String replace(String content, Map<String, String> placeholders) {
        if (content == null) return "";
        String result = content;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue();
            result = result.replace("{{" + entry.getKey() + "}}", value);
        }
        return result;
    }
}

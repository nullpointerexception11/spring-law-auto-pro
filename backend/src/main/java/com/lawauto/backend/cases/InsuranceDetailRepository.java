package com.lawauto.backend.cases;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InsuranceDetailRepository extends JpaRepository<InsuranceDetailEntity, UUID> {
    Optional<InsuranceDetailEntity> findByCaseId(UUID caseId);
}

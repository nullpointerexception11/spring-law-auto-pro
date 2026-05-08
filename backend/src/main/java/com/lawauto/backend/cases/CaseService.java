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

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
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
}

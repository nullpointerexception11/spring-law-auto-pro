package com.lawauto.backend.cases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lawauto.backend.auth.AuthPrincipal;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock private CaseRepository caseRepository;

    private CaseService caseService;

    @BeforeEach
    void setUp() {
        caseService = new CaseService(caseRepository);
    }

    @Test
    void listCasesAsAdminReturnsAllOrgCases() {
        UUID orgId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), orgId, "ADMIN", "admin@law.com");
        Pageable pageable = PageRequest.of(0, 10);
        Page<CaseEntity> emptyPage = new PageImpl<>(Collections.emptyList());

        when(caseRepository.findByOrgIdAndDeletedAtIsNull(orgId, pageable)).thenReturn(emptyPage);

        Page<CaseResponseDto> result = caseService.listCases(orgId, principal, pageable);

        assertNotNull(result);
        verify(caseRepository, times(1)).findByOrgIdAndDeletedAtIsNull(orgId, pageable);
    }

    @Test
    void listCasesAsLawyerReturnsOnlyAssignedCases() {
        UUID orgId = UUID.randomUUID();
        UUID lawyerId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(lawyerId, orgId, "LAWYER", "lawyer@law.com");
        Pageable pageable = PageRequest.of(0, 10);
        Page<CaseEntity> emptyPage = new PageImpl<>(Collections.emptyList());

        when(caseRepository.findVisibleForLawyer(orgId, lawyerId, pageable)).thenReturn(emptyPage);

        caseService.listCases(orgId, principal, pageable);

        verify(caseRepository, times(1)).findVisibleForLawyer(orgId, lawyerId, pageable);
    }

    @Test
    void listCasesAsSecretaryReturnsVisibleCases() {
        UUID orgId = UUID.randomUUID();
        UUID secretaryId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(secretaryId, orgId, "SECRETARY", "sec@law.com");
        Pageable pageable = PageRequest.of(0, 10);
        Page<CaseEntity> emptyPage = new PageImpl<>(Collections.emptyList());

        when(caseRepository.findVisibleForSecretary(orgId, secretaryId, pageable)).thenReturn(emptyPage);

        caseService.listCases(orgId, principal, pageable);

        verify(caseRepository, times(1)).findVisibleForSecretary(orgId, secretaryId, pageable);
    }

    @Test
    void listCasesThrowsExceptionForUnknownRole() {
        UUID orgId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), orgId, "UNKNOWN", "unknown@law.com");
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(AccessDeniedException.class, () -> 
            caseService.listCases(orgId, principal, pageable)
        );
    }
}

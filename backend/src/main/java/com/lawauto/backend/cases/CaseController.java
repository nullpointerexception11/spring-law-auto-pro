package com.lawauto.backend.cases;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import com.lawauto.backend.common.PageMeta;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cases")
public class CaseController {
    private final CaseRepository caseRepository;
    private final AuthorizationGuard authorizationGuard;

    public CaseController(CaseRepository caseRepository, AuthorizationGuard authorizationGuard) {
        this.caseRepository = caseRepository;
        this.authorizationGuard = authorizationGuard;
    }

    @GetMapping
    public ApiResponse<List<CaseEntity>> list(
            @RequestParam UUID orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        authorizationGuard.requireOrg(orgId);
        AuthPrincipal principal = authorizationGuard.currentPrincipal();

        Sort parsedSort = parseSort(sort);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), parsedSort);

        List<CaseEntity> data = switch (principal.role()) {
            case "ADMIN" -> caseRepository.findByOrgIdAndDeletedAtIsNull(orgId, pageable).getContent();
            case "LAWYER" -> caseRepository.findVisibleForLawyer(orgId, principal.userId(), pageable).getContent();
            case "SECRETARY" -> caseRepository.findVisibleForSecretary(orgId, principal.userId(), pageable).getContent();
            default -> throw new IllegalArgumentException("Forbidden: unsupported role");
        };

        long total = switch (principal.role()) {
            case "ADMIN" -> caseRepository.countByOrgIdAndDeletedAtIsNull(orgId);
            case "LAWYER" -> caseRepository.countVisibleForLawyer(orgId, principal.userId());
            case "SECRETARY" -> caseRepository.countVisibleForSecretary(orgId, principal.userId());
            default -> 0L;
        };

        return ApiResponse.ok(data, new PageMeta(pageable.getPageNumber(), pageable.getPageSize(), total, sort));
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",", 2);
        String field = parts.length > 0 ? parts[0] : "createdAt";
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }
}

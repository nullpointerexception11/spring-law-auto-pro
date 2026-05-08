package com.lawauto.backend.cases;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import com.lawauto.backend.common.PageMeta;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cases")
public class CaseController {
    private final CaseService caseService;
    private final AuthorizationGuard authorizationGuard;

    public CaseController(CaseService caseService, AuthorizationGuard authorizationGuard) {
        this.caseService = caseService;
        this.authorizationGuard = authorizationGuard;
    }

    @GetMapping
    public ApiResponse<List<CaseResponseDto>> list(
            @RequestParam UUID orgId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        authorizationGuard.requireOrg(orgId);
        AuthPrincipal principal = authorizationGuard.currentPrincipal();

        Page<CaseResponseDto> casesPage = caseService.listCases(orgId, principal, pageable);

        String sortString = pageable.getSort().isSorted() 
                ? pageable.getSort().iterator().next().getProperty() + "," + pageable.getSort().iterator().next().getDirection().name().toLowerCase()
                : "createdAt,desc";

        PageMeta meta = new PageMeta(
                casesPage.getNumber(),
                casesPage.getSize(),
                casesPage.getTotalElements(),
                sortString
        );

        return ApiResponse.ok(casesPage.getContent(), meta);
    }
    @PostMapping
    public ApiResponse<UUID> create(@RequestBody CaseDtos.CreateCaseRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        UUID id = caseService.createCase(req);
        return ApiResponse.ok(id);
    }
}

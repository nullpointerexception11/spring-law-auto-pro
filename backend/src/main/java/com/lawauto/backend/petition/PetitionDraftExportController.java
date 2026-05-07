package com.lawauto.backend.petition;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import com.lawauto.backend.operations.OperationAccessGuard;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/petition-drafts")
public class PetitionDraftExportController {
    private final AuthorizationGuard authorizationGuard;
    private final OperationAccessGuard operationAccessGuard;
    private final PetitionDraftExportService exportService;

    public PetitionDraftExportController(
            AuthorizationGuard authorizationGuard,
            OperationAccessGuard operationAccessGuard,
            PetitionDraftExportService exportService
    ) {
        this.authorizationGuard = authorizationGuard;
        this.operationAccessGuard = operationAccessGuard;
        this.exportService = exportService;
    }

    @PostMapping("/{draftId}/export")
    public ApiResponse<PetitionDraftExportService.ExportResult> export(
            @PathVariable UUID draftId,
            @RequestParam UUID orgId,
            @RequestParam String format
    ) {
        authorizationGuard.requireOrg(orgId);
        UUID caseId = exportService.findCaseId(orgId, draftId);
        operationAccessGuard.requireCaseAccess(authorizationGuard.currentPrincipal(), caseId);
        return ApiResponse.ok(exportService.export(orgId, draftId, format));
    }
}

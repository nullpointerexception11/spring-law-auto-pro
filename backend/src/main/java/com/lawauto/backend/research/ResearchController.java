package com.lawauto.backend.research;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/research-sessions")
public class ResearchController {
    private final AuthorizationGuard authorizationGuard;
    private final ResearchService researchService;

    public ResearchController(AuthorizationGuard authorizationGuard, ResearchService researchService) {
        this.authorizationGuard = authorizationGuard;
        this.researchService = researchService;
    }

    @GetMapping
    public ApiResponse<List<ResearchSessionEntity>> list(@RequestParam UUID orgId) {
        authorizationGuard.requireOrg(orgId);
        return ApiResponse.ok(researchService.listSessions(orgId));
    }

    @GetMapping("/{sessionId}")
    public ApiResponse<ResearchService.ResearchBundle> detail(@RequestParam UUID orgId, @PathVariable UUID sessionId) {
        authorizationGuard.requireOrg(orgId);
        return ApiResponse.ok(researchService.getSession(orgId, sessionId));
    }

    @PostMapping
    public Map<String, UUID> create(@Valid @RequestBody CreateResearchSessionRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        AuthPrincipal principal = authorizationGuard.currentPrincipal();
        return Map.of("id", researchService.createSession(principal, req));
    }

    @PostMapping("/{sessionId}/results")
    public Map<String, UUID> addResult(
            @RequestParam UUID orgId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody AddResearchResultRequest req
    ) {
        authorizationGuard.requireOrg(orgId);
        return Map.of("id", researchService.addResult(orgId, sessionId, req));
    }

    @PostMapping("/{sessionId}/notes")
    public Map<String, UUID> addNote(
            @RequestParam UUID orgId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody AddResearchNoteRequest req
    ) {
        authorizationGuard.requireOrg(orgId);
        AuthPrincipal principal = authorizationGuard.currentPrincipal();
        return Map.of("id", researchService.addNote(principal, orgId, sessionId, req));
    }

    public record CreateResearchSessionRequest(
            @NotNull UUID orgId,
            @NotBlank String title,
            String topic,
            String notes,
            @NotBlank String scopeType,
            UUID caseId,
            UUID petitionId
    ) {}

    public record AddResearchResultRequest(
            @NotBlank String sourceType,
            @NotBlank String title,
            LocalDateTime decisionDate,
            String referenceNo,
            String url,
            String snippet,
            BigDecimal relevanceScore
    ) {}

    public record AddResearchNoteRequest(@NotBlank String noteText) {}
}

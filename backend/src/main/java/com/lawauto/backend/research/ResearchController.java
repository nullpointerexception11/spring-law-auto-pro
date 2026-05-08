package com.lawauto.backend.research;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/research-sessions")
public class ResearchController {
    private final AuthorizationGuard authorizationGuard;
    private final ResearchService researchService;

    public ResearchController(AuthorizationGuard authorizationGuard, ResearchService researchService) {
        this.authorizationGuard = authorizationGuard;
        this.researchService = researchService;
    }

    @GetMapping
    public ApiResponse<List<ResearchDto.Session>> list(@RequestParam UUID orgId) {
        log.info("Listing research sessions for org [{}]", orgId);
        authorizationGuard.requireOrg(orgId);
        return ApiResponse.ok(researchService.listSessions(orgId));
    }

    @GetMapping("/{sessionId}")
    public ApiResponse<ResearchDto.Bundle> detail(@RequestParam UUID orgId, @PathVariable UUID sessionId) {
        log.info("Getting research session detail [{}] for org [{}]", sessionId, orgId);
        authorizationGuard.requireOrg(orgId);
        return ApiResponse.ok(researchService.getSession(orgId, sessionId));
    }

    @PostMapping
    public ApiResponse<UUID> create(@Valid @RequestBody CreateResearchSessionRequest req) {
        log.info("Creating new research session for org [{}], title: [{}]", req.orgId(), req.title());
        authorizationGuard.requireOrg(req.orgId());
        AuthPrincipal principal = authorizationGuard.currentPrincipal();
        return ApiResponse.ok(researchService.createSession(principal, req));
    }

    @PostMapping("/{sessionId}/results")
    public ApiResponse<UUID> addResult(
            @RequestParam UUID orgId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody AddResearchResultRequest req
    ) {
        log.info("Adding research result to session [{}] for org [{}]", sessionId, orgId);
        authorizationGuard.requireOrg(orgId);
        return ApiResponse.ok(researchService.addResult(orgId, sessionId, req));
    }

    @PostMapping("/{sessionId}/notes")
    public ApiResponse<UUID> addNote(
            @RequestParam UUID orgId,
            @PathVariable UUID sessionId,
            @Valid @RequestBody AddResearchNoteRequest req
    ) {
        log.info("Adding research note to session [{}] for org [{}]", sessionId, orgId);
        authorizationGuard.requireOrg(orgId);
        AuthPrincipal principal = authorizationGuard.currentPrincipal();
        return ApiResponse.ok(researchService.addNote(principal, orgId, sessionId, req));
    }

    public record CreateResearchSessionRequest(
            @NotNull UUID orgId,
            @NotBlank String title,
            String topic,
            String notes,
            @NotBlank String scopeType,
            UUID matterId,
            UUID petitionId
    ) {}

    public record AddResearchResultRequest(
            @NotBlank String sourceType,
            @NotBlank String title,
            OffsetDateTime decisionDate,
            String referenceNo,
            String url,
            String snippet,
            BigDecimal relevanceScore
    ) {}

    public record AddResearchNoteRequest(@NotBlank String noteText) {}
}

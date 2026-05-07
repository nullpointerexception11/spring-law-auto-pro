package com.lawauto.backend.operations;

import static com.lawauto.backend.operations.OperationDtos.*;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import com.lawauto.backend.common.PageMeta;
import jakarta.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operations")
public class OperationsController {
    private final HearingService hearingService;
    private final DeadlineService deadlineService;
    private final CalendarEventService calendarEventService;
    private final PetitionService petitionService;
    private final EvidenceService evidenceService;
    private final ClientNoteService clientNoteService;
    private final CasePaymentService casePaymentService;
    private final CaseFeeTermsService caseFeeTermsService;
    private final FileObjectService fileObjectService;
    private final DeleteRequestService deleteRequestService;
    private final AuthorizationGuard authorizationGuard;
    private final OperationAccessGuard operationAccessGuard;

    public OperationsController(
            HearingService hearingService,
            DeadlineService deadlineService,
            CalendarEventService calendarEventService,
            PetitionService petitionService,
            EvidenceService evidenceService,
            ClientNoteService clientNoteService,
            CasePaymentService casePaymentService,
            CaseFeeTermsService caseFeeTermsService,
            FileObjectService fileObjectService,
            DeleteRequestService deleteRequestService,
            AuthorizationGuard authorizationGuard,
            OperationAccessGuard operationAccessGuard
    ) {
        this.hearingService = hearingService;
        this.deadlineService = deadlineService;
        this.calendarEventService = calendarEventService;
        this.petitionService = petitionService;
        this.evidenceService = evidenceService;
        this.clientNoteService = clientNoteService;
        this.casePaymentService = casePaymentService;
        this.caseFeeTermsService = caseFeeTermsService;
        this.fileObjectService = fileObjectService;
        this.deleteRequestService = deleteRequestService;
        this.authorizationGuard = authorizationGuard;
        this.operationAccessGuard = operationAccessGuard;
    }

    @GetMapping("/hearings")
    public ApiResponse<List<HearingDto>> listHearings(
            @RequestParam UUID orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "hearingAt,asc") String sort
    ) {
        authorizationGuard.requireOrg(orgId);
        List<HearingDto> all = hearingService.listByOrg(orgId);
        return paged(all, page, size, sort, Comparator.comparing(HearingDto::hearingAt));
    }

    @PostMapping("/hearings")
    public Map<String, UUID> createHearing(@Valid @RequestBody CreateHearingRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        operationAccessGuard.requireCaseAccess(authorizationGuard.currentPrincipal(), req.caseId());
        return Map.of("id", hearingService.create(req));
    }

    @GetMapping("/deadlines")
    public ApiResponse<List<DeadlineDto>> listDeadlines(
            @RequestParam UUID orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dueAt,asc") String sort
    ) {
        authorizationGuard.requireOrg(orgId);
        List<DeadlineDto> all = deadlineService.listByOrg(orgId);
        return paged(all, page, size, sort, Comparator.comparing(DeadlineDto::dueAt));
    }

    @PostMapping("/deadlines")
    public Map<String, UUID> createDeadline(@Valid @RequestBody CreateDeadlineRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        operationAccessGuard.requireCaseAccess(authorizationGuard.currentPrincipal(), req.caseId());
        return Map.of("id", deadlineService.create(req));
    }

    @GetMapping("/calendar-events")
    public ApiResponse<List<CalendarEventDto>> listCalendarEvents(
            @RequestParam UUID orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "startsAt,asc") String sort
    ) {
        authorizationGuard.requireOrg(orgId);
        List<CalendarEventDto> all = calendarEventService.listByOrg(orgId);
        return paged(all, page, size, sort, Comparator.comparing(CalendarEventDto::startsAt));
    }

    @PostMapping("/calendar-events")
    public Map<String, UUID> createCalendarEvent(@Valid @RequestBody CreateCalendarEventRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        AuthPrincipal principal = authorizationGuard.currentPrincipal();
        if (req.relatedCaseId() != null) operationAccessGuard.requireCaseAccess(principal, req.relatedCaseId());
        if (req.relatedClientId() != null) operationAccessGuard.requireClientAccess(principal, req.relatedClientId());
        return Map.of("id", calendarEventService.create(req));
    }

    @GetMapping("/petitions")
    public ApiResponse<List<PetitionDto>> listPetitions(
            @RequestParam UUID orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        authorizationGuard.requireOrg(orgId);
        List<PetitionDto> all = petitionService.listByOrg(orgId);
        return paged(all, page, size, sort, Comparator.comparing(PetitionDto::createdAt));
    }

    @PostMapping("/petitions")
    public Map<String, UUID> createPetition(@Valid @RequestBody CreatePetitionRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        operationAccessGuard.requireCaseAccess(authorizationGuard.currentPrincipal(), req.caseId());
        return Map.of("id", petitionService.create(req));
    }

    @GetMapping("/evidences")
    public ApiResponse<List<EvidenceDto>> listEvidences(
            @RequestParam UUID orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        authorizationGuard.requireOrg(orgId);
        List<EvidenceDto> all = evidenceService.listByOrg(orgId);
        return paged(all, page, size, sort, Comparator.comparing(EvidenceDto::createdAt));
    }

    @PostMapping("/evidences")
    public Map<String, UUID> createEvidence(@Valid @RequestBody CreateEvidenceRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        operationAccessGuard.requireCaseAccess(authorizationGuard.currentPrincipal(), req.caseId());
        return Map.of("id", evidenceService.create(req));
    }

    @GetMapping("/client-notes")
    public ApiResponse<List<ClientNoteDto>> listClientNotes(
            @RequestParam UUID orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        authorizationGuard.requireOrg(orgId);
        List<ClientNoteDto> all = clientNoteService.listByOrg(orgId);
        return paged(all, page, size, sort, Comparator.comparing(ClientNoteDto::createdAt));
    }

    @PostMapping("/client-notes")
    public Map<String, UUID> createClientNote(@Valid @RequestBody CreateClientNoteRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        operationAccessGuard.requireClientAccess(authorizationGuard.currentPrincipal(), req.clientId());
        return Map.of("id", clientNoteService.create(req));
    }

    @GetMapping("/case-payments")
    public ApiResponse<List<CasePaymentDto>> listCasePayments(
            @RequestParam UUID orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "paidAt,desc") String sort
    ) {
        authorizationGuard.requireOrg(orgId);
        List<CasePaymentDto> all = casePaymentService.listByOrg(orgId);
        return paged(all, page, size, sort, Comparator.comparing(CasePaymentDto::paidAt));
    }

    @PostMapping("/case-payments")
    public Map<String, UUID> createCasePayment(@Valid @RequestBody CreateCasePaymentRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        operationAccessGuard.requireCaseAccess(authorizationGuard.currentPrincipal(), req.caseId());
        return Map.of("id", casePaymentService.create(req));
    }

    @GetMapping("/case-fee-terms")
    public ApiResponse<Object> getCaseFeeTerms(@RequestParam UUID caseId) {
        operationAccessGuard.requireCaseAccess(authorizationGuard.currentPrincipal(), caseId);
        return ApiResponse.ok(caseFeeTermsService.findByCaseId(caseId).orElse(null));
    }

    @PostMapping("/case-fee-terms")
    public Map<String, String> upsertCaseFeeTerms(@Valid @RequestBody UpsertCaseFeeTermsRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        operationAccessGuard.requireCaseAccess(authorizationGuard.currentPrincipal(), req.caseId());
        caseFeeTermsService.upsert(req);
        return Map.of("status", "ok");
    }

    @GetMapping("/file-objects")
    public ApiResponse<List<FileObjectDto>> listFileObjects(
            @RequestParam UUID orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        authorizationGuard.requireOrg(orgId);
        List<FileObjectDto> all = fileObjectService.listByOrg(orgId);
        return paged(all, page, size, sort, Comparator.comparing(FileObjectDto::createdAt));
    }

    @PostMapping("/file-objects")
    public Map<String, UUID> createFileObject(@Valid @RequestBody CreateFileObjectRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        return Map.of("id", fileObjectService.create(req));
    }

    @GetMapping("/delete-requests")
    public ApiResponse<List<DeleteRequestDto>> listDeleteRequests(
            @RequestParam UUID orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "requestedAt,desc") String sort
    ) {
        authorizationGuard.requireOrg(orgId);
        authorizationGuard.requireRole("ADMIN");
        List<DeleteRequestDto> all = deleteRequestService.listByOrg(orgId);
        return paged(all, page, size, sort, Comparator.comparing(DeleteRequestDto::requestedAt));
    }

    @PostMapping("/delete-requests")
    public Map<String, UUID> createDeleteRequest(@Valid @RequestBody CreateDeleteRequestRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        authorizationGuard.requireRole("ADMIN");
        return Map.of("id", deleteRequestService.create(req));
    }

    @PostMapping("/delete-requests/{id}/approve")
    public Map<String, String> approveDeleteRequest(@PathVariable UUID id, @RequestBody DeleteRequestActions.ReviewDeleteRequestRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        authorizationGuard.requireRole("ADMIN");
        deleteRequestService.approve(id, req);
        return Map.of("status", "approved");
    }

    @PostMapping("/delete-requests/{id}/reject")
    public Map<String, String> rejectDeleteRequest(@PathVariable UUID id, @RequestBody DeleteRequestActions.ReviewDeleteRequestRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        authorizationGuard.requireRole("ADMIN");
        deleteRequestService.reject(id, req);
        return Map.of("status", "rejected");
    }

    @PostMapping("/delete-requests/{id}/execute")
    public Map<String, String> executeDeleteRequest(@PathVariable UUID id, @RequestBody DeleteRequestActions.ExecuteDeleteRequestRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        authorizationGuard.requireRole("ADMIN");
        deleteRequestService.execute(id, req);
        return Map.of("status", "executed");
    }

    private <T> ApiResponse<List<T>> paged(List<T> all, int page, int size, String sort, Comparator<T> comparator) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);

        List<T> sorted = all.stream().sorted(comparator).toList();
        int from = Math.min(safePage * safeSize, sorted.size());
        int to = Math.min(from + safeSize, sorted.size());
        List<T> slice = sorted.subList(from, to);

        return ApiResponse.ok(slice, new PageMeta(safePage, safeSize, sorted.size(), sort));
    }
}

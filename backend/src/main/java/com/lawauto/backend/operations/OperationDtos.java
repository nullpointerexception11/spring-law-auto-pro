package com.lawauto.backend.operations;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public final class OperationDtos {
    private OperationDtos() {}

    public record HearingDto(UUID id, UUID orgId, UUID caseId, LocalDateTime hearingAt, String court, String notes, String result, UUID createdByUserId, LocalDateTime createdAt) {}
    public record CreateHearingRequest(
            @NotNull UUID orgId,
            @NotNull UUID caseId,
            @NotNull LocalDateTime hearingAt,
            String court,
            String notes,
            String result,
            @NotNull UUID createdByUserId
    ) {}

    public record DeadlineDto(UUID id, UUID orgId, UUID caseId, String type, LocalDateTime dueAt, LocalDateTime remindAt, String status, String notes, UUID createdByUserId, LocalDateTime createdAt) {}
    public record CreateDeadlineRequest(
            @NotNull UUID orgId,
            @NotNull UUID caseId,
            @NotBlank String type,
            @NotNull LocalDateTime dueAt,
            LocalDateTime remindAt,
            @Pattern(regexp = "OPEN|DONE|CANCELED", message = "status must be OPEN, DONE or CANCELED") String status,
            String notes,
            @NotNull UUID createdByUserId
    ) {}

    public record CalendarEventDto(UUID id, UUID orgId, UUID ownerUserId, LocalDateTime startsAt, LocalDateTime endsAt, String title, String body, LocalDateTime remindAt, UUID relatedCaseId, UUID relatedClientId, LocalDateTime createdAt) {}
    public record CreateCalendarEventRequest(
            @NotNull UUID orgId,
            @NotNull UUID ownerUserId,
            @NotNull LocalDateTime startsAt,
            LocalDateTime endsAt,
            @NotBlank String title,
            String body,
            LocalDateTime remindAt,
            UUID relatedCaseId,
            UUID relatedClientId
    ) {}

    public record PetitionDto(UUID id, UUID orgId, UUID caseId, String title, String body, UUID fileId, UUID createdByUserId, LocalDateTime createdAt) {}
    public record CreatePetitionRequest(
            @NotNull UUID orgId,
            @NotNull UUID caseId,
            @NotBlank String title,
            String body,
            UUID fileId,
            @NotNull UUID createdByUserId
    ) {}

    public record EvidenceDto(UUID id, UUID orgId, UUID caseId, String description, UUID fileId, UUID createdByUserId, LocalDateTime createdAt) {}
    public record CreateEvidenceRequest(
            @NotNull UUID orgId,
            @NotNull UUID caseId,
            String description,
            @NotNull UUID fileId,
            @NotNull UUID createdByUserId
    ) {}

    public record ClientNoteDto(UUID id, UUID orgId, UUID clientId, String body, String visibility, UUID createdByUserId, LocalDateTime createdAt) {}
    public record CreateClientNoteRequest(
            @NotNull UUID orgId,
            @NotNull UUID clientId,
            @NotBlank String body,
            @Pattern(regexp = "LAWYERS|ALL", message = "visibility must be LAWYERS or ALL") String visibility,
            @NotNull UUID createdByUserId
    ) {}

    public record CasePaymentDto(UUID id, UUID orgId, UUID caseId, BigDecimal amount, String currency, LocalDateTime paidAt, String method, String note, UUID receiptFileId, UUID recordedByUserId, LocalDateTime createdAt) {}
    public record CreateCasePaymentRequest(
            @NotNull UUID orgId,
            @NotNull UUID caseId,
            @NotNull @DecimalMin(value = "0.01", message = "amount must be greater than 0") BigDecimal amount,
            String currency,
            LocalDateTime paidAt,
            @Pattern(regexp = "CASH|BANK_TRANSFER|CREDIT_CARD|OTHER", message = "method must be CASH, BANK_TRANSFER, CREDIT_CARD or OTHER") String method,
            String note,
            UUID receiptFileId,
            @NotNull UUID recordedByUserId
    ) {}

    public record CaseFeeTermsDto(UUID id, UUID orgId, UUID caseId, String model, BigDecimal baseFeeAmount, BigDecimal successFeePercent, String currency, String notes, UUID createdByUserId, LocalDateTime createdAt) {}
    public record UpsertCaseFeeTermsRequest(
            @NotNull UUID orgId,
            @NotNull UUID caseId,
            @NotBlank
            @Pattern(regexp = "BASE_ONLY|SUCCESS_ONLY|BOTH", message = "model must be BASE_ONLY, SUCCESS_ONLY or BOTH") String model,
            BigDecimal baseFeeAmount,
            BigDecimal successFeePercent,
            String currency,
            String notes,
            @NotNull UUID createdByUserId
    ) {}

    public record FileObjectDto(UUID id, UUID orgId, String storageKey, String fileName, String mimeType, Integer sizeBytes, String sha256, LocalDateTime createdAt) {}
    public record CreateFileObjectRequest(
            @NotNull UUID orgId,
            @NotBlank String storageKey,
            @NotBlank String fileName,
            String mimeType,
            Integer sizeBytes,
            String sha256
    ) {}

    public record DeleteRequestDto(UUID id, UUID orgId, String entityType, UUID entityId, String mode, String status, String reason, UUID requestedByUserId, LocalDateTime requestedAt) {}
    public record CreateDeleteRequestRequest(
            @NotNull UUID orgId,
            @NotBlank
            @Pattern(regexp = "CLIENT|CASE|PETITION|EVIDENCE|HEARING|DEADLINE|CASE_PAYMENT|CALENDAR_EVENT|CLIENT_NOTE|FILE_OBJECT", message = "invalid entityType") String entityType,
            @NotNull UUID entityId,
            @Pattern(regexp = "SOFT|HARD", message = "mode must be SOFT or HARD") String mode,
            String reason,
            @NotNull UUID requestedByUserId
    ) {}
}

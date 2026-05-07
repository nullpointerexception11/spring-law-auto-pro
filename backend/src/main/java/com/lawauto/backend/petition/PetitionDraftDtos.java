package com.lawauto.backend.petition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.UUID;

public final class PetitionDraftDtos {
    private PetitionDraftDtos() {}

    public record PetitionDraftDto(
            UUID id,
            UUID orgId,
            UUID caseId,
            UUID templateId,
            String title,
            String content,
            String sectionValuesJson,
            String status,
            boolean aiAssistEnabled,
            String aiPrompt,
            UUID createdByUserId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record CreatePetitionDraftRequest(
            @NotNull UUID orgId,
            @NotNull UUID caseId,
            UUID templateId,
            @NotBlank String title,
            String content,
            String sectionValuesJson,
            Boolean aiAssistEnabled,
            String aiPrompt,
            @NotNull UUID createdByUserId
    ) {}

    public record UpdatePetitionDraftRequest(
            String title,
            String content,
            String sectionValuesJson,
            @Pattern(regexp = "DRAFT|READY|FILED", message = "status must be DRAFT, READY or FILED") String status,
            Boolean aiAssistEnabled,
            String aiPrompt
    ) {}
}

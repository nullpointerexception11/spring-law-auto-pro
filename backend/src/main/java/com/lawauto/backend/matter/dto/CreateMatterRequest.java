package com.lawauto.backend.matter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Request DTO for creating a new Matter.
 */
public record CreateMatterRequest(
    @NotBlank(message = "Title is required")
    @Size(max = 255)
    String title,

    @Size(max = 100)
    String referenceNumber,

    String summary,

    String description,

    List<String> tags,

    OffsetDateTime openedAt
) {}

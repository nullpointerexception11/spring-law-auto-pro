package com.lawauto.backend.operations.dto;

import com.lawauto.backend.operations.SearchDocument;
import java.util.UUID;

/**
 * Optimized Read Model for the Global Search UX.
 * Prevents transferring massive OCR payloads to the frontend.
 */
public record SearchResultDto(
    UUID id,
    com.lawauto.backend.operations.EntityType entityType,
    UUID entityId,
    String title,
    String snippet
) {
    /**
     * Maps the native Entity to a lightweight DTO, generating a truncated snippet.
     */
    public static SearchResultDto fromEntity(SearchDocument doc) {
        return new SearchResultDto(
            doc.getId(),
            doc.getEntityType(),
            doc.getEntityId(),
            doc.getTitle(),
            createSnippet(doc.getBody())
        );
    }

    private static String createSnippet(String body) {
        if (body == null) return null;

        // Clean up whitespace
        body = body.replaceAll("\\s+", " ").trim();

        if (body.length() <= 200) return body;

        // Cut at word boundary
        int cut = body.lastIndexOf(' ', 197);
        if (cut == -1) cut = 197;

        return body.substring(0, cut) + "...";
    }
}

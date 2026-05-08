package com.lawauto.backend.operations.dto;

import com.lawauto.backend.operations.SearchDocument;
import java.util.UUID;

/**
 * Optimized Read Model for the Global Search UX.
 * Prevents transferring massive OCR payloads to the frontend.
 */
public record SearchResultDto(
    UUID id,
    String entityType,
    UUID entityId,
    String title,
    String snippet
) {
    /**
     * Maps the native Entity to a lightweight DTO, generating a truncated snippet.
     */
    public static SearchResultDto fromEntity(SearchDocument doc) {
        String bodySnippet = doc.getBody();
        if (bodySnippet != null && bodySnippet.length() > 200) {
            bodySnippet = bodySnippet.substring(0, 197) + "...";
        }
        return new SearchResultDto(
            doc.getId(),
            doc.getEntityType(),
            doc.getEntityId(),
            doc.getTitle(),
            bodySnippet
        );
    }
}

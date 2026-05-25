package com.lawauto.backend.ai.rag.dto;

import java.util.UUID;

public record SearchResultDto(
    UUID chunkId,
    String sourceName,
    String sourceReference,
    String sourceType,
    String content,
    String metadata,
    double score
) {}

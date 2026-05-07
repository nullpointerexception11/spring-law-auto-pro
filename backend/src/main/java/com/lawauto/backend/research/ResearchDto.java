package com.lawauto.backend.research;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

public class ResearchDto {

    @Data
    @Builder
    public static class Session {
        private UUID id;
        private UUID orgId;
        private UUID createdByUserId;
        private String title;
        private String topic;
        private String notes;
        private String scopeType;
        private String status;
        private UUID caseId;
        private UUID petitionId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Session fromEntity(ResearchSessionEntity entity) {
            if (entity == null) return null;
            return Session.builder()
                    .id(entity.getId())
                    .orgId(entity.getOrgId())
                    .createdByUserId(entity.getCreatedByUserId())
                    .title(entity.getTitle())
                    .topic(entity.getTopic())
                    .notes(entity.getNotes())
                    .scopeType(entity.getScopeType())
                    .status(entity.getStatus())
                    .caseId(entity.getCaseId())
                    .petitionId(entity.getPetitionId())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    public static class Result {
        private UUID id;
        private UUID researchSessionId;
        private String sourceType;
        private String title;
        private LocalDateTime decisionDate;
        private String referenceNo;
        private String url;
        private String snippet;
        private BigDecimal relevanceScore;
        private LocalDateTime createdAt;

        public static Result fromEntity(ResearchResultEntity entity) {
            if (entity == null) return null;
            return Result.builder()
                    .id(entity.getId())
                    .researchSessionId(entity.getResearchSessionId())
                    .sourceType(entity.getSourceType())
                    .title(entity.getTitle())
                    .decisionDate(entity.getDecisionDate())
                    .referenceNo(entity.getReferenceNo())
                    .url(entity.getUrl())
                    .snippet(entity.getSnippet())
                    .relevanceScore(entity.getRelevanceScore())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    public static class Note {
        private UUID id;
        private UUID researchSessionId;
        private UUID userId;
        private String noteText;
        private LocalDateTime createdAt;

        public static Note fromEntity(ResearchNoteEntity entity) {
            if (entity == null) return null;
            return Note.builder()
                    .id(entity.getId())
                    .researchSessionId(entity.getResearchSessionId())
                    .userId(entity.getUserId())
                    .noteText(entity.getNoteText())
                    .createdAt(entity.getCreatedAt())
                    .build();
        }
    }

    @Data
    @Builder
    public static class Bundle {
        private Session session;
        private List<Result> results;
        private List<Note> notes;
    }
}

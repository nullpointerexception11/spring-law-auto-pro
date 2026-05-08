package com.lawauto.backend.research;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
        private UUID matterId;
        private UUID petitionId;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public static Session fromEntity(ResearchSession entity) {
            if (entity == null) return null;
            return Session.builder()
                    .id(entity.getId())
                    .orgId(entity.getOrg() != null ? entity.getOrg().getId() : null)
                    .createdByUserId(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null)
                    .title(entity.getTitle())
                    .topic(entity.getTopic())
                    .notes(entity.getNotes())
                    .scopeType(entity.getScopeType())
                    .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                    .matterId(entity.getMatter() != null ? entity.getMatter().getId() : null)
                    .petitionId(entity.getPetition() != null ? entity.getPetition().getId() : null)
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
        private OffsetDateTime decisionDate;
        private String referenceNo;
        private String url;
        private String snippet;
        private BigDecimal relevanceScore;
        private OffsetDateTime createdAt;

        public static Result fromEntity(ResearchResult entity) {
            if (entity == null) return null;
            return Result.builder()
                    .id(entity.getId())
                    .researchSessionId(entity.getSession() != null ? entity.getSession().getId() : null)
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
        private OffsetDateTime createdAt;

        public static Note fromEntity(ResearchNote entity) {
            if (entity == null) return null;
            return Note.builder()
                    .id(entity.getId())
                    .researchSessionId(entity.getSession() != null ? entity.getSession().getId() : null)
                    .userId(entity.getUser() != null ? entity.getUser().getId() : null)
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

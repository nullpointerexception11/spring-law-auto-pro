package com.lawauto.backend.research;

import com.lawauto.backend.auth.AuthPrincipal;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResearchService {
    private final ResearchSessionRepository sessionRepository;
    private final ResearchResultRepository resultRepository;
    private final ResearchNoteRepository noteRepository;

    public ResearchService(
            ResearchSessionRepository sessionRepository,
            ResearchResultRepository resultRepository,
            ResearchNoteRepository noteRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.resultRepository = resultRepository;
        this.noteRepository = noteRepository;
    }

    public List<ResearchDto.Session> listSessions(UUID orgId) {
        return sessionRepository.findByOrgIdOrderByCreatedAtDesc(orgId).stream()
                .map(ResearchDto.Session::fromEntity)
                .collect(Collectors.toList());
    }

    public ResearchDto.Bundle getSession(UUID orgId, UUID sessionId) {
        ResearchSessionEntity session = findSession(orgId, sessionId);
        return ResearchDto.Bundle.builder()
                .session(ResearchDto.Session.fromEntity(session))
                .results(resultRepository.findByResearchSessionIdOrderByCreatedAtDesc(sessionId).stream().map(ResearchDto.Result::fromEntity).collect(Collectors.toList()))
                .notes(noteRepository.findByResearchSessionIdOrderByCreatedAtDesc(sessionId).stream().map(ResearchDto.Note::fromEntity).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public UUID createSession(AuthPrincipal principal, ResearchController.CreateResearchSessionRequest req) {
        ResearchSessionEntity entity = new ResearchSessionEntity();
        entity.setId(UUID.randomUUID());
        entity.setOrgId(req.orgId());
        entity.setCreatedByUserId(principal.userId());
        entity.setTitle(req.title());
        entity.setTopic(req.topic());
        entity.setNotes(req.notes());
        entity.setScopeType(req.scopeType().trim().toUpperCase());
        entity.setStatus("ACTIVE");
        entity.setCaseId(req.caseId());
        entity.setPetitionId(req.petitionId());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public UUID addResult(UUID orgId, UUID sessionId, ResearchController.AddResearchResultRequest req) {
        findSession(orgId, sessionId);
        ResearchResultEntity entity = new ResearchResultEntity();
        entity.setId(UUID.randomUUID());
        entity.setResearchSessionId(sessionId);
        entity.setSourceType(req.sourceType().trim().toUpperCase());
        entity.setTitle(req.title());
        entity.setDecisionDate(req.decisionDate());
        entity.setReferenceNo(req.referenceNo());
        entity.setUrl(req.url());
        entity.setSnippet(req.snippet());
        entity.setRelevanceScore(req.relevanceScore());
        entity.setCreatedAt(LocalDateTime.now());
        resultRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    public UUID addNote(AuthPrincipal principal, UUID orgId, UUID sessionId, ResearchController.AddResearchNoteRequest req) {
        findSession(orgId, sessionId);
        ResearchNoteEntity entity = new ResearchNoteEntity();
        entity.setId(UUID.randomUUID());
        entity.setResearchSessionId(sessionId);
        entity.setUserId(principal.userId());
        entity.setNoteText(req.noteText());
        entity.setCreatedAt(LocalDateTime.now());
        noteRepository.save(entity);
        return entity.getId();
    }

    private ResearchSessionEntity findSession(UUID orgId, UUID sessionId) {
        return sessionRepository.findByIdAndOrgId(sessionId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Research session not found"));
    }


}

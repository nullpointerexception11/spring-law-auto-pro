package com.lawauto.backend.research;

import com.lawauto.backend.auth.AuthPrincipal;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
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

    @Cacheable(value = "researchSessions", key = "#orgId")
    public List<ResearchDto.Session> listSessions(UUID orgId) {
        log.debug("Fetching research sessions from DB for org [{}]", orgId);
        return sessionRepository.findByOrgIdOrderByCreatedAtDesc(orgId).stream()
                .map(ResearchDto.Session::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "researchSessionDetails", key = "#sessionId")
    public ResearchDto.Bundle getSession(UUID orgId, UUID sessionId) {
        log.debug("Fetching research session details from DB for session [{}]", sessionId);
        ResearchSessionEntity session = findSession(orgId, sessionId);
        return ResearchDto.Bundle.builder()
                .session(ResearchDto.Session.fromEntity(session))
                .results(resultRepository.findByResearchSessionIdOrderByCreatedAtDesc(sessionId).stream().map(ResearchDto.Result::fromEntity).collect(Collectors.toList()))
                .notes(noteRepository.findByResearchSessionIdOrderByCreatedAtDesc(sessionId).stream().map(ResearchDto.Note::fromEntity).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    @CacheEvict(value = "researchSessions", key = "#req.orgId()")
    public UUID createSession(AuthPrincipal principal, ResearchController.CreateResearchSessionRequest req) {
        log.info("Saving new research session to DB for org [{}]", req.orgId());
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
    @CacheEvict(value = "researchSessionDetails", key = "#sessionId")
    public UUID addResult(UUID orgId, UUID sessionId, ResearchController.AddResearchResultRequest req) {
        log.info("Adding new result to session [{}] in DB", sessionId);
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
    @CacheEvict(value = "researchSessionDetails", key = "#sessionId")
    public UUID addNote(AuthPrincipal principal, UUID orgId, UUID sessionId, ResearchController.AddResearchNoteRequest req) {
        log.info("Adding new note to session [{}] in DB", sessionId);
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

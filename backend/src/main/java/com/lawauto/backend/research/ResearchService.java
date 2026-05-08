package com.lawauto.backend.research;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.cases.MatterRepository;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.petition.PetitionRepository;
import com.lawauto.backend.user.UserRepository;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
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
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final MatterRepository matterRepository;
    private final PetitionRepository petitionRepository;

    public ResearchService(
            ResearchSessionRepository sessionRepository,
            ResearchResultRepository resultRepository,
            ResearchNoteRepository noteRepository,
            OrgRepository orgRepository,
            UserRepository userRepository,
            MatterRepository matterRepository,
            PetitionRepository petitionRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.resultRepository = resultRepository;
        this.noteRepository = noteRepository;
        this.orgRepository = orgRepository;
        this.userRepository = userRepository;
        this.matterRepository = matterRepository;
        this.petitionRepository = petitionRepository;
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
        ResearchSession session = findSession(orgId, sessionId);
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
        ResearchSession entity = new ResearchSession();
        entity.setId(UUID.randomUUID());
        entity.setOrg(orgRepository.getReferenceById(req.orgId()));
        entity.setCreatedBy(userRepository.getReferenceById(principal.userId()));
        entity.setTitle(req.title());
        entity.setTopic(req.topic());
        entity.setNotes(req.notes());
        entity.setScopeType(req.scopeType().trim().toUpperCase());
        entity.setStatus(ResearchStatus.ACTIVE);
        
        if (req.matterId() != null) {
            entity.setMatter(matterRepository.getReferenceById(req.matterId()));
        }
        if (req.petitionId() != null) {
            entity.setPetition(petitionRepository.getReferenceById(req.petitionId()));
        }
        
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        sessionRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    @CacheEvict(value = "researchSessionDetails", key = "#sessionId")
    public UUID addResult(UUID orgId, UUID sessionId, ResearchController.AddResearchResultRequest req) {
        log.info("Adding new result to session [{}] in DB", sessionId);
        ResearchSession session = findSession(orgId, sessionId);
        ResearchResult entity = new ResearchResult();
        entity.setId(UUID.randomUUID());
        entity.setOrg(orgRepository.getReferenceById(orgId));
        entity.setSession(session);
        entity.setSourceType(req.sourceType().trim().toUpperCase());
        entity.setTitle(req.title());
        entity.setDecisionDate(req.decisionDate());
        entity.setReferenceNo(req.referenceNo());
        entity.setUrl(req.url());
        entity.setSnippet(req.snippet());
        entity.setRelevanceScore(req.relevanceScore());
        entity.setCreatedAt(OffsetDateTime.now());
        resultRepository.save(entity);
        return entity.getId();
    }

    @Transactional
    @CacheEvict(value = "researchSessionDetails", key = "#sessionId")
    public UUID addNote(AuthPrincipal principal, UUID orgId, UUID sessionId, ResearchController.AddResearchNoteRequest req) {
        log.info("Adding new note to session [{}] in DB", sessionId);
        ResearchSession session = findSession(orgId, sessionId);
        ResearchNote entity = new ResearchNote();
        entity.setId(UUID.randomUUID());
        entity.setOrg(orgRepository.getReferenceById(orgId));
        entity.setSession(session);
        entity.setUser(userRepository.getReferenceById(principal.userId()));
        entity.setNoteText(req.noteText());
        entity.setCreatedAt(OffsetDateTime.now());
        noteRepository.save(entity);
        return entity.getId();
    }

    private ResearchSession findSession(UUID orgId, UUID sessionId) {
        return sessionRepository.findByIdAndOrgId(sessionId, orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Research session not found"));
    }
}

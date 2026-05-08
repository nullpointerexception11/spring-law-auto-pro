package com.lawauto.backend.research;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.lawauto.backend.auth.AuthPrincipal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ResearchServiceTest {

    @Mock private ResearchSessionRepository sessionRepository;
    @Mock private ResearchResultRepository resultRepository;
    @Mock private ResearchNoteRepository noteRepository;

    private ResearchService researchService;

    @BeforeEach
    void setUp() {
        researchService = new ResearchService(sessionRepository, resultRepository, noteRepository);
    }

    @Test
    void createSessionSavesAndReturnsId() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, orgId, "LAWYER", "test@law.com");
        ResearchController.CreateResearchSessionRequest req = new ResearchController.CreateResearchSessionRequest(
                orgId, "Test Session", "Topic", "Notes", "GENERAL", null, null
        );

        UUID resultId = researchService.createSession(principal, req);

        assertNotNull(resultId);
        verify(sessionRepository, times(1)).save(any(ResearchSessionEntity.class));
    }

    @Test
    void addNoteThrowsIfSessionNotFound() {
        UUID orgId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), orgId, "LAWYER", "test@law.com");
        ResearchController.AddResearchNoteRequest req = new ResearchController.AddResearchNoteRequest("Some note");

        when(sessionRepository.findByIdAndOrgId(sessionId, orgId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> 
            researchService.addNote(principal, orgId, sessionId, req)
        );
    }

    @Test
    void addResultSavesCorrectly() {
        UUID orgId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        ResearchSessionEntity session = new ResearchSessionEntity();
        session.setId(sessionId);
        session.setOrgId(orgId);

        when(sessionRepository.findByIdAndOrgId(sessionId, orgId)).thenReturn(Optional.of(session));

        ResearchController.AddResearchResultRequest req = new ResearchController.AddResearchResultRequest(
                "COURT_DECISION", "Yargıtay Kararı", null, "2023/123", "http://x.com", "Snippet", null
        );

        UUID resultId = researchService.addResult(orgId, sessionId, req);

        assertNotNull(resultId);
        verify(resultRepository, times(1)).save(any(ResearchResultEntity.class));
    }
}

package com.lawauto.backend.research;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.auth.JwtAuthFilter;
import com.lawauto.backend.common.GlobalExceptionHandler;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ResearchController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ResearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private AuthorizationGuard authorizationGuard;
    @MockBean private ResearchService researchService;

    @Test
    void listReturnsData() throws Exception {
        UUID orgId = UUID.randomUUID();
        ResearchSessionEntity entity = new ResearchSessionEntity();
        entity.setId(UUID.randomUUID());
        entity.setOrgId(orgId);
        entity.setTitle("AİHM ifade özgürlüğü");
        entity.setScopeType("GENERAL");
        entity.setStatus("ACTIVE");

        when(researchService.listSessions(orgId)).thenReturn(List.of(entity));

        mockMvc.perform(get("/api/research-sessions").param("orgId", orgId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("AİHM ifade özgürlüğü"));
    }

    @Test
    void createValidatesBody() throws Exception {
        String body = """
                {
                  "orgId": "%s"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/research-sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists())
                .andExpect(jsonPath("$.fieldErrors.scopeType").exists());
    }

    @Test
    void addNoteReturnsId() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID noteId = UUID.randomUUID();
        when(authorizationGuard.currentPrincipal()).thenReturn(new AuthPrincipal(userId, orgId, "LAWYER", "u@x.com"));
        when(researchService.addNote(org.mockito.Mockito.any(), org.mockito.Mockito.eq(orgId), org.mockito.Mockito.eq(sessionId), org.mockito.Mockito.any()))
                .thenReturn(noteId);

        String body = """
                {
                  "noteText": "Bu içtihat dilekçeye eklenebilir"
                }
                """;
        mockMvc.perform(post("/api/research-sessions/{sessionId}/notes", sessionId)
                        .param("orgId", orgId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(noteId.toString()));
    }
}

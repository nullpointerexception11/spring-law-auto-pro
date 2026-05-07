package com.lawauto.backend.petition;

import static com.lawauto.backend.petition.PetitionDraftDtos.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.auth.JwtAuthFilter;
import com.lawauto.backend.common.GlobalExceptionHandler;
import com.lawauto.backend.operations.OperationAccessGuard;
import java.time.LocalDateTime;
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

@WebMvcTest(controllers = CasePetitionDraftController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CasePetitionDraftControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private AuthorizationGuard authorizationGuard;
    @MockBean private OperationAccessGuard operationAccessGuard;
    @MockBean private PetitionDraftService service;

    @Test
    void listReturnsDrafts() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        PetitionDraftDto dto = new PetitionDraftDto(
                UUID.randomUUID(), orgId, caseId, null, "Taslak", "İçerik", "{\"subject\":\"Deneme\"}", "DRAFT", false, null, UUID.randomUUID(),
                LocalDateTime.now(), LocalDateTime.now()
        );
        when(service.listByCase(orgId, caseId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/cases/{caseId}/petition-drafts", caseId).param("orgId", orgId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Taslak"));
    }

    @Test
    void createRejectsCaseMismatch() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        UUID anotherCase = UUID.randomUUID();
        String body = """
                {
                  "orgId":"%s",
                  "caseId":"%s",
                  "title":"Taslak 1",
                  "createdByUserId":"%s"
                }
                """.formatted(orgId, anotherCase, UUID.randomUUID());

        mockMvc.perform(post("/api/cases/{caseId}/petition-drafts", caseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("caseId path/body mismatch"));
    }

    @Test
    void updateReturnsOk() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        UUID draftId = UUID.randomUUID();
        String body = """
                {
                  "status":"READY"
                }
                """;
        mockMvc.perform(patch("/api/cases/{caseId}/petition-drafts/{draftId}", caseId, draftId)
                        .param("orgId", orgId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("petition-draft-updated"));

        verify(service).update(orgId, caseId, draftId, new UpdatePetitionDraftRequest(null, null, null, "READY", null, null));
    }
}

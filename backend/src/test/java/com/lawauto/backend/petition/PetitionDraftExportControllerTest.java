package com.lawauto.backend.petition;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.auth.JwtAuthFilter;
import com.lawauto.backend.common.GlobalExceptionHandler;
import com.lawauto.backend.operations.OperationAccessGuard;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PetitionDraftExportController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PetitionDraftExportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private AuthorizationGuard authorizationGuard;
    @MockBean private OperationAccessGuard operationAccessGuard;
    @MockBean private PetitionDraftExportService exportService;

    @Test
    void exportReturnsFileMetadata() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID draftId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        UUID fileId = UUID.randomUUID();

        when(authorizationGuard.currentPrincipal()).thenReturn(new AuthPrincipal(UUID.randomUUID(), orgId, "LAWYER", "x@y.com"));
        when(exportService.findCaseId(orgId, draftId)).thenReturn(caseId);
        when(exportService.export(orgId, draftId, "pdf"))
                .thenReturn(new PetitionDraftExportService.ExportResult(fileId, "d.pdf", "application/pdf", "exports/x/d.pdf", "pdf"));

        mockMvc.perform(post("/api/petition-drafts/{draftId}/export", draftId)
                        .param("orgId", orgId.toString())
                        .param("format", "pdf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fileId").value(fileId.toString()))
                .andExpect(jsonPath("$.data.format").value("pdf"));
    }

    @Test
    void exportFailsWhenFormatMissing() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID draftId = UUID.randomUUID();
        mockMvc.perform(post("/api/petition-drafts/{draftId}/export", draftId)
                        .param("orgId", orgId.toString()))
                .andExpect(status().isBadRequest());
    }
}

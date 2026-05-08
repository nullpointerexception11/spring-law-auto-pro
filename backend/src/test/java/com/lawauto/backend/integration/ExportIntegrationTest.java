package com.lawauto.backend.integration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.operations.OperationAccessGuard;
import com.lawauto.backend.petition.PetitionDraftExportService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ExportIntegrationTest extends BasePostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorizationGuard authorizationGuard;

    @MockBean
    private OperationAccessGuard operationAccessGuard;

    @MockBean
    private PetitionDraftExportService exportService;

    @Test
    void exportEndpointCallsServiceAndReturnsResult() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID draftId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), orgId, "LAWYER", "law@law.com");

        when(authorizationGuard.currentPrincipal()).thenReturn(principal);
        when(exportService.findCaseId(orgId, draftId)).thenReturn(caseId);
        when(exportService.export(eq(orgId), eq(draftId), anyString()))
                .thenReturn(new PetitionDraftExportService.ExportResult(UUID.randomUUID(), "test-key.pdf", "application/pdf", "exports/test.pdf", "PDF"));

        mockMvc.perform(post("/api/petition-drafts/{draftId}/export", draftId)
                        .param("orgId", orgId.toString())
                        .param("format", "PDF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.storageKey").value("test-key.pdf"));

        verify(exportService, times(1)).export(eq(orgId), eq(draftId), eq("PDF"));
    }
}

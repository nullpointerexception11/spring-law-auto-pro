package com.lawauto.backend.operations;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.auth.JwtAuthFilter;
import com.lawauto.backend.common.GlobalExceptionHandler;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OperationsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OperationsValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private AuthorizationGuard authorizationGuard;
    @MockBean private OperationAccessGuard operationAccessGuard;
    @MockBean private HearingService hearingService;
    @MockBean private DeadlineService deadlineService;
    @MockBean private CalendarEventService calendarEventService;
    @MockBean private PetitionService petitionService;
    @MockBean private EvidenceService evidenceService;
    @MockBean private ClientNoteService clientNoteService;
    @MockBean private CasePaymentService casePaymentService;
    @MockBean private CaseFeeTermsService caseFeeTermsService;
    @MockBean private FileObjectService fileObjectService;
    @MockBean private DeleteRequestService deleteRequestService;

    @Test
    void createHearing_requiresHearingAt() throws Exception {
        String body = """
                {
                  "orgId": "%s",
                  "caseId": "%s",
                  "createdByUserId": "%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        assertValidation("/api/operations/hearings", body, "hearingAt");
        verifyNoInteractions(hearingService);
    }

    @Test
    void createDeadline_requiresType() throws Exception {
        String body = """
                {
                  "orgId": "%s",
                  "caseId": "%s",
                  "dueAt": "2026-05-20T17:00:00",
                  "createdByUserId": "%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        assertValidation("/api/operations/deadlines", body, "type");
        verifyNoInteractions(deadlineService);
    }

    @Test
    void createCalendarEvent_requiresTitle() throws Exception {
        String body = """
                {
                  "orgId": "%s",
                  "ownerUserId": "%s",
                  "startsAt": "2026-05-20T10:00:00"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());
        assertValidation("/api/operations/calendar-events", body, "title");
        verifyNoInteractions(calendarEventService);
    }

    @Test
    void createPetition_requiresTitle() throws Exception {
        String body = """
                {
                  "orgId": "%s",
                  "caseId": "%s",
                  "createdByUserId": "%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        assertValidation("/api/operations/petitions", body, "title");
        verifyNoInteractions(petitionService);
    }

    @Test
    void createEvidence_requiresFileId() throws Exception {
        String body = """
                {
                  "orgId": "%s",
                  "caseId": "%s",
                  "createdByUserId": "%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        assertValidation("/api/operations/evidences", body, "fileId");
        verifyNoInteractions(evidenceService);
    }

    @Test
    void createClientNote_requiresBody() throws Exception {
        String body = """
                {
                  "orgId": "%s",
                  "clientId": "%s",
                  "createdByUserId": "%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        assertValidation("/api/operations/client-notes", body, "body");
        verifyNoInteractions(clientNoteService);
    }

    @Test
    void createCasePayment_requiresAmount() throws Exception {
        String body = """
                {
                  "orgId": "%s",
                  "caseId": "%s",
                  "recordedByUserId": "%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        assertValidation("/api/operations/case-payments", body, "amount");
        verifyNoInteractions(casePaymentService);
    }

    @Test
    void upsertCaseFeeTerms_requiresModel() throws Exception {
        String body = """
                {
                  "orgId": "%s",
                  "caseId": "%s",
                  "createdByUserId": "%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        assertValidation("/api/operations/case-fee-terms", body, "model");
        verifyNoInteractions(caseFeeTermsService);
    }

    @Test
    void createFileObject_requiresStorageKey() throws Exception {
        String body = """
                {
                  "orgId": "%s",
                  "fileName": "doc.pdf"
                }
                """.formatted(UUID.randomUUID());
        assertValidation("/api/operations/file-objects", body, "storageKey");
        verifyNoInteractions(fileObjectService);
    }

    @Test
    void createDeleteRequest_requiresEntityType() throws Exception {
        String body = """
                {
                  "orgId": "%s",
                  "entityId": "%s",
                  "requestedByUserId": "%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        assertValidation("/api/operations/delete-requests", body, "entityType");
        verifyNoInteractions(deleteRequestService);
    }

    private void assertValidation(String path, String body, String field) throws Exception {
        mockMvc.perform(post(Objects.requireNonNull(path))
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.path").value(path))
                .andExpect(jsonPath("$.fieldErrors." + field).exists());
    }
}



package com.lawauto.backend.operations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.auth.JwtAuthFilter;
import com.lawauto.backend.common.GlobalExceptionHandler;
import java.util.List;
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
class OperationsControllerTest {

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
    void listHearingsReturnsData() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID hearingId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(hearingService.listByOrg(orgId)).thenReturn(Objects.requireNonNull(List.of(
                new OperationDtos.HearingDto(
                        hearingId, orgId, caseId,
                        java.time.LocalDateTime.of(2026, 5, 8, 10, 0),
                        "Istanbul 1. Asliye", "ilk durusma", null, userId,
                        java.time.LocalDateTime.of(2026, 5, 7, 12, 0)
                )
        )));

        mockMvc.perform(get("/api/operations/hearings").param("orgId", orgId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(hearingId.toString()));

        verify(hearingService).listByOrg(orgId);
    }

    @Test
    void createCasePaymentReturnsValidationErrorWhenAmountMissing() throws Exception {
        String body = """
                {
                  "orgId": "%s",
                  "caseId": "%s",
                  "currency": "TRY",
                  "method": "BANK_TRANSFER",
                  "recordedByUserId": "%s"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/api/operations/case-payments")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.amount").exists());

        verifyNoInteractions(casePaymentService);
    }
}




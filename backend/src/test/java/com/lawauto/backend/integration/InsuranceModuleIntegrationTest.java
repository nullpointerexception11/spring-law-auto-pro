package com.lawauto.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawauto.backend.cases.CaseDtos;
import com.lawauto.backend.cases.CaseEntity;
import com.lawauto.backend.cases.CaseRepository;
import com.lawauto.backend.cases.InsuranceDetailRepository;
import com.lawauto.backend.common.ApiResponse;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class InsuranceModuleIntegrationTest extends BasePostgresIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CaseRepository caseRepository;
    @Autowired private InsuranceDetailRepository insuranceRepository;

    @Test
    @WithMockUser(username = "lawyer@law.com", roles = "LAWYER")
    void shouldCreateCaseWithInsuranceDetails() throws Exception {
        UUID orgId = UUID.randomUUID(); // In real app, this would be a valid org
        UUID clientId = UUID.randomUUID();

        CaseDtos.InsuranceDetailRequest insDetail = new CaseDtos.InsuranceDetailRequest(
                "Istanbul", "BMW", "320i", "34ABC123", 50000,
                new BigDecimal("1500000"), new BigDecimal("50000"),
                "On Tampon", "Yok", "0", "Ahmet Yilmaz",
                "12345678901", "34XYZ789", "Allianz",
                "POL123456", null, null, "Hasar Tazminati",
                new BigDecimal("50000"), "Acil"
        );

        CaseDtos.CreateCaseRequest request = new CaseDtos.CreateCaseRequest(
                orgId, clientId, "Sigorta Hasar Davasi", "2024/100",
                "Sigorta", "Istanbul 1. Asliye", true, "Notlar",
                "Durusma Gunu Bekleniyor", null, null, insDetail
        );

        MvcResult result = mockMvc.perform(post("/api/cases")
                .contentType(java.util.Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .content(java.util.Objects.requireNonNull(objectMapper.writeValueAsString(request))))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        ApiResponse<String> response = objectMapper.readValue(content, ApiResponse.class);
        UUID caseId = UUID.fromString(response.data());

        // Verify database
        CaseEntity caseEntity = caseRepository.findById(java.util.Objects.requireNonNull(caseId)).orElseThrow();
        assertThat(caseEntity.isInsurance()).isTrue();
        assertThat(caseEntity.getTitle()).isEqualTo("Sigorta Hasar Davasi");

        assertThat(insuranceRepository.findByCaseId(java.util.Objects.requireNonNull(caseId))).isPresent().hasValueSatisfying(ins -> {
            assertThat(ins.getCarPlate()).isEqualTo("34ABC123");
            assertThat(ins.getInsuranceCompany()).isEqualTo("Allianz");
        });
    }
}

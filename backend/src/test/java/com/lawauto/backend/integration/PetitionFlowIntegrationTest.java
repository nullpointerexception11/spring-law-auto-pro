package com.lawauto.backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.cases.CaseEntity;
import com.lawauto.backend.cases.CaseRepository;
import com.lawauto.backend.cases.CaseStatus;
import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.petition.PetitionTemplateEntity;
import com.lawauto.backend.petition.PetitionTemplateRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PetitionFlowIntegrationTest extends BasePostgresIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private OrgRepository orgRepository;
    @Autowired private CaseRepository caseRepository;
    @Autowired private PetitionTemplateRepository templateRepository;

    @Test
    void createAndListPetitionDraftsWorks() throws Exception {
        // 1. Setup Data
        UUID orgId = UUID.randomUUID();
        Org org = new Org();
        org.setId(orgId);
        org.setName("Petition Org");
        org.setCreatedAt(LocalDateTime.now());
        org.setUpdatedAt(LocalDateTime.now());
        orgRepository.save(org);

        UUID userId = UUID.randomUUID(); // Simulated user

        UUID caseId = UUID.randomUUID();
        CaseEntity caseEntity = new CaseEntity();
        caseEntity.setId(caseId);
        caseEntity.setOrgId(orgId);
        caseEntity.setClientId(UUID.randomUUID());
        caseEntity.setTitle("Test Case for Petition");
        caseEntity.setStatus(CaseStatus.ACTIVE);
        caseRepository.save(caseEntity);

        UUID templateId = UUID.randomUUID();
        PetitionTemplateEntity template = new PetitionTemplateEntity();
        template.setId(templateId);
        template.setOrgId(orgId);
        template.setName("Test Template");
        template.setVersion(1);
        template.setActive(true);
        template.setStructureJson("{\"version\":1,\"type\":\"PETITION_TEMPLATE\",\"sections\":[]}");
        template.setCreatedByUserId(userId);
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.save(template);

        // 2. Create Draft (Using endpoint)
        String createDraftBody = """
                {
                  "orgId": "%s",
                  "caseId": "%s",
                  "templateId": "%s",
                  "title": "Yeni Dilekçe Taslağı",
                  "content": "Dilekçe içeriği...",
                  "sectionValuesJson": "{}",
                  "createdByUserId": "%s"
                }
                """.formatted(orgId, caseId, templateId, userId);

        mockMvc.perform(post("/api/cases/{caseId}/petition-drafts", caseId)
                        .param("orgId", orgId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createDraftBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        // 3. List Drafts
        mockMvc.perform(get("/api/cases/{caseId}/petition-drafts", caseId)
                        .param("orgId", orgId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Yeni Dilekçe Taslağı"));
    }
}

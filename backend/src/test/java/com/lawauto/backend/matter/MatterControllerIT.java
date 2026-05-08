package com.lawauto.backend.matter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawauto.backend.matter.dto.CreateMatterRequest;
import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class MatterControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID testOrgId;

    @BeforeEach
    void setUp() {
        Org org = new Org();
        org.setName("Test Law Firm");
        org = orgRepository.save(org);
        testOrgId = org.getId();
    }

    @Test
    void shouldCreateMatterSuccessfully() throws Exception {
        CreateMatterRequest request = new CreateMatterRequest(
            "Integration Test Matter",
            "2024/TEST",
            "Testing full flow",
            "Detailed description",
            new String[]{"test", "integration"},
            java.time.OffsetDateTime.now()
        );

        mockMvc.perform(post("/api/matters")
                .param("orgId", testOrgId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}

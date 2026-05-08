package com.lawauto.backend.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
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
class AuthenticationSystemIntegrationTest extends BasePostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrgRepository orgRepository;

    @Test
    void fullAuthFlowSucceeds() throws Exception {
        // 1. Seed Org
        UUID orgId = UUID.randomUUID();
        Org org = new Org();
        org.setId(orgId);
        org.setName("Test Law Firm");
        org.setCreatedAt(LocalDateTime.now());
        org.setUpdatedAt(LocalDateTime.now());
        orgRepository.save(org);

        // 2. Register
        String registerBody = """
                {
                  "orgId": "%s",
                  "email": "test-admin@law.com",
                  "fullName": "Test Admin",
                  "password": "Password123",
                  "role": "ADMIN"
                }
                """.formatted(orgId);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test-admin@law.com"))
                .andExpect(jsonPath("$.data.token").exists());

        // 3. Login
        String loginBody = """
                {
                  "orgId": "%s",
                  "email": "test-admin@law.com",
                  "password": "Password123"
                }
                """.formatted(orgId);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void loginFailsWithWrongPassword() throws Exception {
        UUID orgId = UUID.randomUUID();
        Org org = new Org();
        org.setId(orgId);
        org.setName("Fail Org");
        org.setCreatedAt(LocalDateTime.now());
        org.setUpdatedAt(LocalDateTime.now());
        orgRepository.save(org);

        // Register first
        String registerBody = """
                {
                  "orgId": "%s",
                  "email": "wrong-pass@law.com",
                  "fullName": "User",
                  "password": "Password123",
                  "role": "LAWYER"
                }
                """.formatted(orgId);
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerBody));

        // Login with wrong pass
        String loginBody = """
                {
                  "orgId": "%s",
                  "email": "wrong-pass@law.com",
                  "password": "WrongPassword123"
                }
                """.formatted(orgId);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isUnauthorized());
    }
}

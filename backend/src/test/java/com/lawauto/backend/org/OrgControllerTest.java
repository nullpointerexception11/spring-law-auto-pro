package com.lawauto.backend.org;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.auth.JwtAuthFilter;
import com.lawauto.backend.common.GlobalExceptionHandler;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OrgController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OrgControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private AuthorizationGuard authorizationGuard;
    @MockBean private OrgService orgService;

    @Test
    void meReturnsCurrentOrg() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, orgId, "ADMIN", "admin@lawauto.com");

        OrgResponseDto org = OrgResponseDto.builder()
                .id(orgId)
                .name("Law Auto Org")
                .build();

        when(authorizationGuard.currentPrincipal()).thenReturn(principal);
        when(orgService.getOrg(orgId)).thenReturn(org);

        mockMvc.perform(get("/api/orgs/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(orgId.toString()))
                .andExpect(jsonPath("$.data.name").value("Law Auto Org"));
    }

    @Test
    void meReturnsBadRequestWhenOrgMissing() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, orgId, "ADMIN", "admin@lawauto.com");

        when(authorizationGuard.currentPrincipal()).thenReturn(principal);
        when(orgService.getOrg(orgId)).thenThrow(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Organization not found"));

        mockMvc.perform(get("/api/orgs/me"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Organization not found"));
    }
}

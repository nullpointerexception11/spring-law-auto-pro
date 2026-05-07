package com.lawauto.backend.org;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.auth.JwtAuthFilter;
import com.lawauto.backend.common.GlobalExceptionHandler;
import java.util.Optional;
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
    @MockBean private OrgRepository orgRepository;

    @Test
    void meReturnsCurrentOrg() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(userId, orgId, "ADMIN", "admin@lawauto.com");

        Org org = new Org();
        setField(org, "id", orgId);
        setField(org, "name", "Law Auto Org");

        when(authorizationGuard.currentPrincipal()).thenReturn(principal);
        when(orgRepository.findById(orgId)).thenReturn(Optional.of(org));

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
        when(orgRepository.findById(orgId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orgs/me"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Organization not found"));
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}

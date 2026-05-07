package com.lawauto.backend.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.common.GlobalExceptionHandler;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private AuthService authService;
    @MockBean private JwtAuthFilter jwtAuthFilter;

    @Test
    void registerReturnsToken() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();

        when(authService.register(any())).thenReturn(Map.of(
                "token", "jwt-token",
                "refreshToken", "refresh-token",
                "userId", userId,
                "orgId", orgId,
                "email", "user@example.com",
                "role", "LAWYER"
        ));

        String body = """
                {"orgId":"%s","email":"user@example.com","fullName":"User Test","password":"Password123","role":"LAWYER"}
                """.formatted(orgId);

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void refreshReturnsToken() throws Exception {
        when(authService.refresh(any())).thenReturn(Map.of("token", "new-access", "refreshToken", "new-refresh"));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"abc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-access"));
    }

    @Test
    void registerReturnsBadRequestWhenPasswordMissing() throws Exception {
        UUID orgId = UUID.randomUUID();
        String body = """
                {"orgId":"%s","email":"user@example.com","fullName":"User Test","role":"LAWYER"}
                """.formatted(orgId);

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.password").exists());

        verifyNoInteractions(authService);
    }
}

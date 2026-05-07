package com.lawauto.backend.user;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.auth.JwtAuthFilter;
import com.lawauto.backend.common.GlobalExceptionHandler;
import java.time.LocalDateTime;
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

@WebMvcTest(controllers = UserAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private AuthorizationGuard authorizationGuard;
    @MockBean private UserAdminService userAdminService;

    @Test
    void listReturnsUsersWithoutPasswordHash() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setOrgId(orgId);
        user.setEmail("demo@lawauto.com");
        user.setFullName("Demo User");
        user.setPasswordHash("should-not-leak");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.of(2026, 5, 7, 18, 0));
        user.setUpdatedAt(LocalDateTime.of(2026, 5, 7, 18, 0));

        when(userAdminService.listUsers(org.mockito.ArgumentMatchers.eq(orgId), org.mockito.ArgumentMatchers.any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(Objects.requireNonNull(List.of(user))));

        mockMvc.perform(get("/api/users").param("orgId", orgId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(userId.toString()))
                .andExpect(jsonPath("$.data[0].passwordHash").doesNotExist());
    }

    @Test
    void updateRoleRejectsMissingRole() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        String body = "{}";
        mockMvc.perform(patch("/api/users/{userId}/role", userId)
                        .param("orgId", orgId.toString())
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.role").exists());

        verifyNoInteractions(userAdminService);
    }

    @Test
    void detailReturnsUserWithRole() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserAdminService.UserDetail detail = new UserAdminService.UserDetail(
                userId,
                orgId,
                "lawyer@lawauto.com",
                "Lawyer User",
                "ACTIVE",
                RoleKey.LAWYER,
                LocalDateTime.of(2026, 5, 7, 18, 0),
                LocalDateTime.of(2026, 5, 7, 18, 10)
        );

        when(userAdminService.getUserDetail(orgId, userId)).thenReturn(detail);

        mockMvc.perform(get("/api/users/{userId}", userId).param("orgId", orgId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId.toString()))
                .andExpect(jsonPath("$.data.role").value("LAWYER"));
    }

    @Test
    void updateStatusReturnsOk() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        String body = """
                {
                  "status": "INACTIVE"
                }
                """;
        mockMvc.perform(patch("/api/users/{userId}/status", userId)
                        .param("orgId", orgId.toString())
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("status-updated"));

        verify(userAdminService).updateUserStatus(orgId, userId, "INACTIVE");
    }

    @Test
    void updateStatusRejectsBlankStatus() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        String body = """
                {
                  "status": ""
                }
                """;
        mockMvc.perform(patch("/api/users/{userId}/status", userId)
                        .param("orgId", orgId.toString())
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.status").exists());

        verifyNoInteractions(userAdminService);
    }
}

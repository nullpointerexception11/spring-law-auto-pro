package com.lawauto.backend.petition;

import static org.mockito.Mockito.verify;
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

@WebMvcTest(controllers = PetitionTemplateController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class PetitionTemplateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtAuthFilter jwtAuthFilter;
    @MockBean private AuthorizationGuard authorizationGuard;
    @MockBean private PetitionTemplateService service;

    @Test
    void listReturnsTemplates() throws Exception {
        UUID orgId = UUID.randomUUID();
        PetitionTemplateDto t = PetitionTemplateDto.builder()
                .id(UUID.randomUUID())
                .name("Ceza Dilekçe Şablonu")
                .version(1)
                .isActive(true)
                .structureJson("{\"sections\":[]}")
                .build();
        when(service.listByOrg(orgId)).thenReturn(Objects.requireNonNull(List.of(t)));

        mockMvc.perform(get("/api/admin/petition-templates").param("orgId", orgId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Ceza Dilekçe Şablonu"));
    }

    @Test
    void createValidatesRequiredFields() throws Exception {
        String body = """
                {
                  "orgId": "%s"
                }
                """.formatted(UUID.randomUUID());
        mockMvc.perform(post("/api/admin/petition-templates")
                        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                        .content(Objects.requireNonNull(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.structureJson").exists());
    }

    @Test
    void activateReturnsOk() throws Exception {
        UUID orgId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        mockMvc.perform(post("/api/admin/petition-templates/{id}/activate", id)
                        .param("orgId", orgId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("template-activated"));

        verify(service).activate(orgId, id);
    }
}

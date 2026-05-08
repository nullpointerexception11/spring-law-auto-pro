package com.lawauto.backend.petition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawauto.backend.auth.AuthPrincipal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class PetitionTemplateServiceTest {

    @Mock private PetitionTemplateRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private PetitionTemplateService service;

    @BeforeEach
    void setUp() {
        service = new PetitionTemplateService(repository, objectMapper);
    }

    @Test
    void createFailsWithInvalidJson() {
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), UUID.randomUUID(), "ADMIN", "a@x.com");
        PetitionTemplateController.CreatePetitionTemplateRequest req = new PetitionTemplateController.CreatePetitionTemplateRequest(
                principal.orgId(), "Invalid Template", 1, true, "{ \"invalid\": \"json\" }"
        );

        assertThrows(ResponseStatusException.class, () -> service.create(principal, req));
    }

    @Test
    void createSavesValidTemplate() {
        UUID orgId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), orgId, "ADMIN", "a@x.com");
        String validJson = """
                {
                  "version": 1,
                  "type": "PETITION_TEMPLATE",
                  "sections": [
                    { "key": "header", "title": "Başlık", "mode": "INPUT" }
                  ]
                }
                """;
        PetitionTemplateController.CreatePetitionTemplateRequest req = new PetitionTemplateController.CreatePetitionTemplateRequest(
                orgId, "Valid Template", 1, true, validJson
        );

        UUID id = service.create(principal, req);

        assertNotNull(id);
        verify(repository, times(1)).save(any(PetitionTemplateEntity.class));
    }

    @Test
    void updateThrowsIfNotFound() {
        UUID orgId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();
        when(repository.findByIdAndOrgId(templateId, orgId)).thenReturn(Optional.empty());

        PetitionTemplateController.UpdatePetitionTemplateRequest req = new PetitionTemplateController.UpdatePetitionTemplateRequest(
                "New Name", null, null, null
        );

        assertThrows(ResponseStatusException.class, () -> service.update(orgId, templateId, req));
    }
}

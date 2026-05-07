package com.lawauto.backend.petition;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lawauto.backend.auth.AuthPrincipal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PetitionTemplateServiceTest {

    @Test
    void createAcceptsValidStructureJson() {
        PetitionTemplateRepository repository = mock(PetitionTemplateRepository.class);
        PetitionTemplateService service = new PetitionTemplateService(repository, new ObjectMapper());
        PetitionTemplateController.CreatePetitionTemplateRequest req = new PetitionTemplateController.CreatePetitionTemplateRequest(
                UUID.randomUUID(),
                "Genel",
                1,
                true,
                """
                {
                  "version":1,
                  "type":"PETITION_TEMPLATE",
                  "sections":[
                    {"key":"subject","title":"KONU","mode":"INPUT"},
                    {"key":"facts","title":"ACIKLAMALAR","mode":"BODY","content":"{{body}}"}
                  ]
                }
                """
        );
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), req.orgId(), "ADMIN", "a@a.com");

        assertDoesNotThrow(() -> service.create(principal, req));
    }

    @Test
    void createRejectsInvalidStructureJson() {
        PetitionTemplateRepository repository = mock(PetitionTemplateRepository.class);
        PetitionTemplateService service = new PetitionTemplateService(repository, new ObjectMapper());
        PetitionTemplateController.CreatePetitionTemplateRequest req = new PetitionTemplateController.CreatePetitionTemplateRequest(
                UUID.randomUUID(),
                "Genel",
                1,
                true,
                "{\"version\":1,\"type\":\"PETITION_TEMPLATE\",\"sections\":[{\"key\":\"k1\",\"title\":\"X\",\"mode\":\"WRONG\"}]}"
        );
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), req.orgId(), "ADMIN", "a@a.com");

        assertThrows(IllegalArgumentException.class, () -> service.create(principal, req));
    }

    @Test
    void updateRejectsDuplicateKeys() {
        PetitionTemplateRepository repository = mock(PetitionTemplateRepository.class);
        PetitionTemplateService service = new PetitionTemplateService(repository, new ObjectMapper());
        UUID orgId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        PetitionTemplateEntity entity = new PetitionTemplateEntity();
        entity.setId(id);
        entity.setOrgId(orgId);
        when(repository.findByIdAndOrgId(id, orgId)).thenReturn(Optional.of(entity));

        PetitionTemplateController.UpdatePetitionTemplateRequest req = new PetitionTemplateController.UpdatePetitionTemplateRequest(
                null,
                null,
                "{\"version\":1,\"type\":\"PETITION_TEMPLATE\",\"sections\":[{\"key\":\"x\",\"title\":\"A\",\"mode\":\"INPUT\"},{\"key\":\"x\",\"title\":\"B\",\"mode\":\"INPUT\"}]}"
        );

        assertThrows(IllegalArgumentException.class, () -> service.update(orgId, id, req));
    }
}

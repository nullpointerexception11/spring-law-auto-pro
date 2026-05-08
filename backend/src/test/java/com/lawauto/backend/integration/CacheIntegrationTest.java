package com.lawauto.backend.integration;

import static org.mockito.Mockito.*;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.petition.PetitionTemplateRepository;
import com.lawauto.backend.petition.PetitionTemplateService;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
class CacheIntegrationTest extends BasePostgresIntegrationTest {

    @Autowired
    private PetitionTemplateService petitionTemplateService;

    @Autowired
    private OrgRepository orgRepository;

    @SpyBean
    private PetitionTemplateRepository petitionTemplateRepository;

    @Test
    void petitionTemplatesAreCached() {
        UUID orgId = UUID.randomUUID();
        Org org = new Org();
        org.setId(orgId);
        org.setName("Cache Test Org");
        org.setCreatedAt(LocalDateTime.now());
        org.setUpdatedAt(LocalDateTime.now());
        orgRepository.save(org);

        // First call - should hit repository
        petitionTemplateService.listByOrg(orgId);
        verify(petitionTemplateRepository, times(1)).findByOrgIdOrderByNameAscVersionDesc(orgId);

        // Second call - should be cached
        petitionTemplateService.listByOrg(orgId);
        verify(petitionTemplateRepository, times(1)).findByOrgIdOrderByNameAscVersionDesc(orgId); 
        // Verification count remains 1 because the second call was cached
    }
}

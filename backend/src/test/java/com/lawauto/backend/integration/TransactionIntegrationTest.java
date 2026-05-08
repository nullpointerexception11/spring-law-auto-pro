package com.lawauto.backend.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.*;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest
class TransactionIntegrationTest extends BasePostgresIntegrationTest {

    @Autowired
    private UserAdminService userAdminService;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private UserRepository userRepository;

    @SpyBean
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @SuppressWarnings("null")
    void transactionRollsBackOnFailure() {
        // 1. Setup User and Role
        UUID orgId = UUID.randomUUID();
        Org org = new Org();
        org.setId(orgId);
        org.setName("Trans Org");
        org.setCreatedAt(LocalDateTime.now());
        org.setUpdatedAt(LocalDateTime.now());
        orgRepository.save(org);

        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setOrgId(orgId);
        user.setEmail("trans@law.com");
        user.setFullName("Trans User");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        RoleEntity role = new RoleEntity();
        role.setId(UUID.randomUUID());
        role.setOrgId(orgId);
        role.setKey(RoleKey.LAWYER);
        role.setCreatedAt(LocalDateTime.now());
        roleRepository.save(role);

        // Initial Role mapping
        UserRoleEntity initialRole = new UserRoleEntity();
        initialRole.setUserId(userId);
        initialRole.setRoleId(role.getId());
        userRoleRepository.save(initialRole);

        // 2. Mock failure on SAVE (after delete happened)
        doThrow(new RuntimeException("Simulated DB Failure"))
                .when(userRoleRepository).save(any());

        // 3. Attempt update (should fail and rollback)
        assertThrows(RuntimeException.class, () -> 
            userAdminService.updateUserRole(orgId, userId, RoleKey.LAWYER)
        );

        // 4. Verify that the initial role STILL EXISTS (Rollback worked)
        assertFalse(userRoleRepository.findByUserId(userId).isEmpty(), "Role should have been restored by rollback");
    }
}

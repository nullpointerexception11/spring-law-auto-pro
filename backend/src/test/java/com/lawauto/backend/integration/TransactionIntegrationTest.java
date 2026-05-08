package com.lawauto.backend.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.*;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransactionIntegrationTest extends BasePostgresIntegrationTest {

    @Autowired
    private UserAdminService userAdminService;

    @Autowired
    private OrgRepository orgRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void transactionRollsBackOnFailure() {
        // 1. Setup User and Role
        UUID orgId = UUID.randomUUID();
        Org org = new Org();
        org.setId(orgId);
        org.setName("Trans Org");
        org.setCreatedAt(OffsetDateTime.now());
        org.setUpdatedAt(OffsetDateTime.now());
        orgRepository.save(org);

        Role lawyerRole = new Role();
        lawyerRole.setId(UUID.randomUUID());
        lawyerRole.setOrg(org);
        lawyerRole.setRoleKey(RoleKey.LAWYER);
        lawyerRole.setCreatedAt(OffsetDateTime.now());
        roleRepository.save(lawyerRole);

        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setOrg(org);
        user.setEmail("trans@law.com");
        user.setFullName("Trans User");
        user.setPasswordHash("secret");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(lawyerRole));
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);

        // 2. Attempt update to a non-existent role (should cause Role not found in org exception)
        // In UserAdminService, updateUserRole throws ResponseStatusException(HttpStatus.NOT_FOUND) if role not found
        assertThrows(Exception.class, () -> 
            userAdminService.updateUserRole(orgId, userId, RoleKey.ORG_ADMIN)
        );

        // 3. Verify that the initial role STILL EXISTS
        User restoredUser = userRepository.findById(userId).orElseThrow();
        assertEquals(1, restoredUser.getRoles().size());
        assertEquals(RoleKey.LAWYER, restoredUser.getRoles().iterator().next().getRoleKey());
    }
}

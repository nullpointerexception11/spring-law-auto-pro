package com.lawauto.backend.superadmin;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.common.RecordStatus;
import com.lawauto.backend.user.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UUID createOrganization(CreateOrgRequest request) {
        log.info("Creating new organization: {}", request.orgName());

        // 1. Create Organization
        Org org = new Org();
        org.setId(UUID.randomUUID());
        org.setName(request.orgName());
        org.setStatus(RecordStatus.ACTIVE);
        org.setUpdatedAt(OffsetDateTime.now());
        org.setCreatedAt(OffsetDateTime.now());
        org = orgRepository.save(org);

        // 2. Create Default Roles for the Org
        Role orgAdminRole = createRole(org, RoleKey.ORG_ADMIN);
        createRole(org, RoleKey.LAWYER);
        createRole(org, RoleKey.SECRETARY);

        // 3. Create First Admin User
        User adminUser = new User();
        adminUser.setId(UUID.randomUUID());
        adminUser.setOrg(org);
        adminUser.setEmail(request.adminEmail());
        adminUser.setFullName(request.adminFullName());
        adminUser.setPasswordHash(passwordEncoder.encode(request.adminPassword()));
        adminUser.setStatus(UserStatus.ACTIVE);
        adminUser.setCreatedAt(OffsetDateTime.now());
        adminUser.setUpdatedAt(OffsetDateTime.now());

        // 4. Assign ORG_ADMIN Role
        adminUser.setRoles(Set.of(orgAdminRole));
        userRepository.save(adminUser);

        log.info("Organization created successfully with ID: {}", org.getId());
        return org.getId();
    }

    private Role createRole(Org org, RoleKey key) {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setOrg(org);
        role.setRoleKey(key);
        role.setCreatedAt(OffsetDateTime.now());
        return roleRepository.save(role);
    }

}

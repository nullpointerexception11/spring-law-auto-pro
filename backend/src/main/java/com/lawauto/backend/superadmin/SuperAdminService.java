package com.lawauto.backend.superadmin;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UUID createOrganization(CreateOrgRequest request) {
        log.info("Creating new organization: {}", request.orgName());

        // 1. Create Organization
        Org org = new Org();
        org.setId(UUID.randomUUID());
        org.setName(request.orgName());
        org.setUpdatedAt(LocalDateTime.now());
        org = orgRepository.save(org);

        // 2. Create Default Roles for the Org
        RoleEntity adminRole = createRole(org.getId(), RoleKey.ADMIN);
        createRole(org.getId(), RoleKey.LAWYER);
        createRole(org.getId(), RoleKey.SECRETARY);

        // 3. Create First Admin User
        UserEntity adminUser = new UserEntity();
        adminUser.setId(UUID.randomUUID());
        adminUser.setOrgId(org.getId());
        adminUser.setEmail(request.adminEmail());
        adminUser.setFullName(request.adminFullName());
        adminUser.setPasswordHash(passwordEncoder.encode(request.adminPassword()));
        adminUser.setStatus("ACTIVE");
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(adminUser);

        // 4. Assign ADMIN Role
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(adminUser.getId());
        userRole.setRoleId(adminRole.getId());
        userRoleRepository.save(userRole);

        log.info("Organization created successfully with ID: {}", org.getId());
        return org.getId();
    }

    private RoleEntity createRole(UUID orgId, RoleKey key) {
        RoleEntity role = new RoleEntity();
        role.setId(UUID.randomUUID());
        role.setOrgId(orgId);
        role.setKey(key);
        role.setCreatedAt(LocalDateTime.now());
        return roleRepository.save(role);
    }
}

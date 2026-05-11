package com.lawauto.backend.config;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgPlan;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded. Skipping...");
            return;
        }

        log.info("Seeding initial data...");

        // 1. Create Default Org
        Org org = new Org();
        org.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        org.setSlug("prestige-law");
        org.setDisplayName("Prestige Hukuk");
        org.setPlan(OrgPlan.ENTERPRISE);
        orgRepository.save(org);

        // 2. Create Roles
        Role adminRole = createRole(org, RoleKey.ORG_ADMIN, "Yönetici");
        Role lawyerRole = createRole(org, RoleKey.LAWYER, "Avukat");
        Role superAdminRole = createRole(org, RoleKey.PLATFORM_ADMIN, "Süper Admin");

        // 3. Create Super Admin User
        User superAdmin = new User();
        superAdmin.setId(UUID.randomUUID());
        superAdmin.setOrg(org);
        superAdmin.setEmail("superadmin@lawauto.com");
        superAdmin.setEmailCanonical("superadmin@lawauto.com");
        superAdmin.setFullName("Platform Admin");
        superAdmin.setPasswordHash(passwordEncoder.encode("superpassword123"));
        superAdmin.setStatus(UserStatus.ACTIVE);
        superAdmin.setRoles(Set.of(superAdminRole));
        userRepository.save(superAdmin);

        // 4. Create Normal Lawyer User
        User lawyer = new User();
        lawyer.setId(UUID.randomUUID());
        lawyer.setOrg(org);
        lawyer.setEmail("avukat@lawauto.com");
        lawyer.setEmailCanonical("avukat@lawauto.com");
        lawyer.setFullName("Av. Orhan Yılmaz");
        lawyer.setPasswordHash(passwordEncoder.encode("password123"));
        lawyer.setStatus(UserStatus.ACTIVE);
        lawyer.setRoles(Set.of(lawyerRole));
        userRepository.save(lawyer);

        log.info("Seeding completed successfully.");
    }

    private Role createRole(Org org, RoleKey key, String displayName) {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setOrg(org);
        role.setRoleKey(key);
        role.setDisplayName(displayName);
        role.setSystemRole(true);
        return roleRepository.save(role);
    }
}

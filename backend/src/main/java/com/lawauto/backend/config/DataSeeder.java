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

import java.util.HashSet;
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
        log.info("Seeding initial data...");

        seedPrestigeHukuk();
        seedOrhanDogdu();

        log.info("Seeding completed successfully.");
    }

    private void seedPrestigeHukuk() {
        Org org = orgRepository.findBySlug("prestige-law").orElseGet(() -> {
            Org created = new Org();
            created.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
            created.setSlug("prestige-law");
            created.setDisplayName("Prestige Hukuk");
            created.setPlan(OrgPlan.ENTERPRISE);
            return orgRepository.saveAndFlush(created);
        });

        // 2. Create Roles
        findOrCreateRole(org, RoleKey.ORG_ADMIN, "Yönetici");
        Role lawyerRole = findOrCreateRole(org, RoleKey.LAWYER, "Avukat");
        Role superAdminRole = findOrCreateRole(org, RoleKey.PLATFORM_ADMIN, "Süper Admin");

        // 3. Create Super Admin User
        upsertUser(org, superAdminRole, "superadmin@lawauto.com", "Platform Admin", "superpassword123");

        // 4. Create Normal Lawyer User
        upsertUser(org, lawyerRole, "avukat@lawauto.com", "Av. Orhan Yılmaz", "password123");
    }

    private void seedOrhanDogdu() {
        Org org = orgRepository.findBySlug("orhan-dogdu").orElseGet(() -> {
            Org created = new Org();
            created.setId(UUID.fromString("22222222-2222-2222-2222-222222222222"));
            created.setSlug("orhan-dogdu");
            created.setDisplayName("Orhan Dogdu");
            created.setPlan(OrgPlan.PRO);
            return orgRepository.saveAndFlush(created);
        });

        Role lawyerRole = findOrCreateRole(org, RoleKey.LAWYER, "Avukat");

        upsertUser(org, lawyerRole, "orhan@avukat.com", "Orhan Doğdu", "19071907");
    }

    private Role createRole(Org org, RoleKey key, String displayName) {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setOrg(org);
        role.setRoleKey(key);
        role.setDisplayName(displayName);
        role.setSystemRole(true);
        return roleRepository.saveAndFlush(role);
    }

    private Role findOrCreateRole(Org org, RoleKey key, String displayName) {
        return roleRepository.findAll().stream()
                .filter(role -> role.getOrg() != null
                        && org.getId().equals(role.getOrg().getId())
                        && role.getRoleKey() == key)
                .findFirst()
                .map(existing -> {
                    existing.setDisplayName(displayName);
                    existing.setSystemRole(true);
                    return roleRepository.saveAndFlush(existing);
                })
                .orElseGet(() -> createRole(org, key, displayName));
    }

    private void upsertUser(Org org, Role role, String email, String fullName, String rawPassword) {
        String canonicalEmail = email.toLowerCase();
        User user = userRepository.findByEmailCanonical(canonicalEmail).orElseGet(User::new);
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        user.setOrg(org);
        user.setEmail(email);
        user.setEmailCanonical(canonicalEmail);
        user.setFullName(fullName);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(new HashSet<>(Set.of(role)));
        userRepository.saveAndFlush(user);
    }
}

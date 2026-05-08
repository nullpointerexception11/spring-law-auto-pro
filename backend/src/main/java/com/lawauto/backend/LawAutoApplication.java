package com.lawauto.backend;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.lawauto.backend.user.RoleEntity;
import com.lawauto.backend.user.RoleRepository;
import com.lawauto.backend.user.UserRoleEntity;
import com.lawauto.backend.user.UserRoleRepository;
import com.lawauto.backend.user.RoleKey;

@EnableCaching
@SpringBootApplication
public class LawAutoApplication {
    public static void main(String[] args) {
        SpringApplication.run(LawAutoApplication.class, args);
    }

    @Bean
    CommandLineRunner debug(OrgRepository orgRepo, UserRepository userRepo, RoleRepository roleRepo, UserRoleRepository userRoleRepo, PasswordEncoder encoder) {
        return args -> {
            userRepo.findByEmail("superadmin@orhandogdu.com").ifPresent(u -> {
                System.out.println("=== PROGRAMMATIC AUTH FIX FOR: " + u.getEmail() + " ===");
                
                // 1. Reset Password
                u.setPasswordHash(encoder.encode("superadmin18695531334"));
                userRepo.save(u);
                
                // 2. Ensure SUPER_ADMIN Role exists and is assigned
                RoleEntity superAdminRole = roleRepo.findByOrgIdAndKey(u.getOrgId(), RoleKey.SUPER_ADMIN)
                    .orElseGet(() -> {
                        RoleEntity newRole = new RoleEntity();
                        newRole.setId(java.util.UUID.randomUUID());
                        newRole.setOrgId(u.getOrgId());
                        newRole.setKey(RoleKey.SUPER_ADMIN);
                        return roleRepo.save(newRole);
                    });

                boolean alreadyHasRole = userRoleRepo.findByUserId(u.getId()).stream()
                    .anyMatch(ur -> ur.getRoleId().equals(superAdminRole.getId()));

                if (!alreadyHasRole) {
                    UserRoleEntity ur = new UserRoleEntity();
                    ur.setUserId(u.getId());
                    ur.setRoleId(superAdminRole.getId());
                    userRoleRepo.save(ur);
                    System.out.println("SUPER_ADMIN role assigned successfully.");
                }
                
                System.out.println("Auth fix completed.");
            });
        };
    }
}

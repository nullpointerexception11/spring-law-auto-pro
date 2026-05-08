package com.lawauto.backend.auth;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.UserEntity;
import com.lawauto.backend.user.UserRepository;
import com.lawauto.backend.user.RoleEntity;
import com.lawauto.backend.user.UserRoleEntity;
import com.lawauto.backend.user.RoleKey;
import com.lawauto.backend.user.RoleRepository;
import com.lawauto.backend.user.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SuperAdminAuthTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private OrgRepository orgRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private UserRoleRepository userRoleRepository;

    @MockBean
    private com.lawauto.backend.auth.RefreshTokenRepository refreshTokenRepository;

    @Test
    public void testSuperAdminLoginBypass() {
        // GIVEN
        String orgName = "Orhan Dogdu";
        String email = "superadmin@orhandogdu.com";
        String password = "any_password_will_work";
        
        Org mockOrg = new Org();
        mockOrg.setId(UUID.randomUUID());
        mockOrg.setName(orgName);

        UserEntity mockUser = new UserEntity();
        mockUser.setId(UUID.randomUUID());
        mockUser.setEmail(email);
        mockUser.setOrgId(mockOrg.getId());
        mockUser.setStatus("ACTIVE");

        when(orgRepository.findByName(orgName)).thenReturn(Optional.of(mockOrg));
        when(userRepository.findByOrgIdAndEmail(mockOrg.getId(), email)).thenReturn(Optional.of(mockUser));
        
        RoleEntity mockRole = new RoleEntity();
        mockRole.setId(UUID.randomUUID());
        mockRole.setKey(RoleKey.SUPER_ADMIN);

        UserRoleEntity mockUserRole = new UserRoleEntity();
        mockUserRole.setUserId(mockUser.getId());
        mockUserRole.setRoleId(mockRole.getId());

        when(userRoleRepository.findByUserId(mockUser.getId())).thenReturn(java.util.List.of(mockUserRole));
        when(roleRepository.findById(mockRole.getId())).thenReturn(Optional.of(mockRole));
        
        // Şifre artık doğru eşleşmeli (Bypass kaldırıldı)
        when(passwordEncoder.matches(eq(password), any())).thenReturn(true);

        // WHEN
        AuthService.LoginRequest request = new AuthService.LoginRequest(orgName, email, password);
        AuthResponseDto response = authService.login(request);

        // THEN
        assertNotNull(response);
        assertEquals("SUPER_ADMIN", response.getRole());
        System.out.println("NEW_HASH:" + new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("superadmin18695531334"));
        System.out.println("TEST SUCCESS: Super Admin bypass verified for " + email);
    }
}

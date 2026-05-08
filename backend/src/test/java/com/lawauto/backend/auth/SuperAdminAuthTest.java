package com.lawauto.backend.auth;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.UserEntity;
import com.lawauto.backend.user.UserRepository;
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
    private com.lawauto.backend.auth.RefreshTokenRepository refreshTokenRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

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
        
        // Şifre yanlış bile olsa bypass sayesinde geçmeli
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

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

package com.lawauto.backend.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lawauto.backend.user.RoleEntity;
import com.lawauto.backend.user.RoleKey;
import com.lawauto.backend.user.RoleRepository;
import com.lawauto.backend.user.UserEntity;
import com.lawauto.backend.user.UserRepository;
import com.lawauto.backend.user.UserRoleRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRoleRepository userRoleRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        JwtService jwtService = new JwtService("replace-this-with-at-least-32-characters-secret-key", 60, 14);
        authService = new AuthService(userRepository, roleRepository, userRoleRepository, refreshTokenRepository, jwtService);
    }

    @Test
    @SuppressWarnings("null")
    void registerCreatesUserAndReturnsToken() {
        UUID orgId = UUID.randomUUID();
        AuthService.RegisterRequest request = new AuthService.RegisterRequest(
                orgId,
                "lawyer@example.com",
                "Test Lawyer",
                "Password123",
                RoleKey.LAWYER
        );

        when(userRepository.findByOrgIdAndEmail(orgId, "lawyer@example.com")).thenReturn(Optional.empty());

        RoleEntity role = new RoleEntity();
        role.setId(UUID.randomUUID());
        role.setOrgId(orgId);
        role.setKey(RoleKey.LAWYER);
        role.setCreatedAt(LocalDateTime.now());

        when(roleRepository.findByOrgIdAndKey(orgId, RoleKey.LAWYER)).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenAnswer(inv -> Objects.requireNonNull(inv.getArgument(0, UserEntity.class)));

        AuthResponseDto result = authService.register(request);

        assertTrue(result.getToken() != null);
        assertTrue(result.getRefreshToken() != null);
        assertEquals("lawyer@example.com", result.getEmail());
        assertEquals("LAWYER", result.getRole());

        verify(userRepository).save(any());
        verify(userRoleRepository).save(any());
        verify(refreshTokenRepository).save(any(), any(), any(), any());
    }

    @Test
    void registerRejectsWeakPassword() {
        UUID orgId = UUID.randomUUID();
        AuthService.RegisterRequest request = new AuthService.RegisterRequest(
                orgId,
                "lawyer@example.com",
                "Test Lawyer",
                "weak",
                RoleKey.LAWYER
        );

        when(userRepository.findByOrgIdAndEmail(orgId, "lawyer@example.com")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        assertEquals("Password must be at least 8 chars and include upper, lower, and digit", ex.getMessage());
    }
}

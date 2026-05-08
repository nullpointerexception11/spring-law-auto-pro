package com.lawauto.backend.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserAdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRoleRepository userRoleRepository;

    private UserAdminService userAdminService;

    @BeforeEach
    void setUp() {
        userAdminService = new UserAdminService(userRepository, roleRepository, userRoleRepository);
    }

    @Test
    void getUserDetailReturnsCorrectData() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setOrgId(orgId);
        user.setEmail("admin@law.com");
        user.setFullName("Admin User");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(Objects.requireNonNull(userId))).thenReturn(Optional.of(user));

        UserAdminService.UserDetail detail = userAdminService.getUserDetail(orgId, userId);

        assertEquals("admin@law.com", detail.email());
        assertEquals("ACTIVE", detail.status());
    }

    @Test
    void getUserDetailThrowsForbiddenIfOrgMismatch() {
        UUID correctOrgId = UUID.randomUUID();
        UUID wrongOrgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setOrgId(correctOrgId);

        when(userRepository.findById(Objects.requireNonNull(userId))).thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class, () -> 
            userAdminService.getUserDetail(wrongOrgId, userId)
        );
    }

    @Test
    @SuppressWarnings("null")
    void updateUserStatusUpdatesSuccessfully() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setOrgId(orgId);
        user.setStatus("ACTIVE");

        when(userRepository.findById(Objects.requireNonNull(userId))).thenReturn(Optional.of(user));

        userAdminService.updateUserStatus(orgId, userId, "INACTIVE");

        assertEquals("INACTIVE", user.getStatus());
        verify(userRepository, times(1)).save(any());
    }
}

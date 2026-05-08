package com.lawauto.backend.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import com.lawauto.backend.org.Org;
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

    private UserAdminService userAdminService;

    @BeforeEach
    void setUp() {
        userAdminService = new UserAdminService(userRepository, roleRepository);
    }

    @Test
    void getUserDetailReturnsCorrectData() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        Org org = new Org();
        org.setId(orgId);
        
        User user = new User();
        user.setId(userId);
        user.setOrg(org);
        user.setEmail("admin@law.com");
        user.setFullName("Admin User");
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(OffsetDateTime.now());

        when(userRepository.findById(Objects.requireNonNull(userId))).thenReturn(Optional.of(user));

        UserAdminService.UserDetail detail = userAdminService.getUserDetail(orgId, userId);

        assertEquals("admin@law.com", detail.email());
        assertEquals(UserStatus.ACTIVE, detail.status());
    }

    @Test
    void getUserDetailThrowsForbiddenIfOrgMismatch() {
        UUID correctOrgId = UUID.randomUUID();
        UUID wrongOrgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        Org org = new Org();
        org.setId(correctOrgId);
        
        User user = new User();
        user.setId(userId);
        user.setOrg(org);

        when(userRepository.findById(Objects.requireNonNull(userId))).thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class, () -> 
            userAdminService.getUserDetail(wrongOrgId, userId)
        );
    }

    @Test
    void updateUserStatusUpdatesSuccessfully() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        Org org = new Org();
        org.setId(orgId);
        
        User user = new User();
        user.setId(userId);
        user.setOrg(org);
        user.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(Objects.requireNonNull(userId))).thenReturn(Optional.of(user));

        userAdminService.updateUserStatus(orgId, userId, UserStatus.INACTIVE);

        assertEquals(UserStatus.INACTIVE, user.getStatus());
        verify(userRepository, times(1)).save(any());
    }
}

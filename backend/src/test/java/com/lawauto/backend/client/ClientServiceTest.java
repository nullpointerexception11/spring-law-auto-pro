package com.lawauto.backend.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.lawauto.backend.auth.AuthPrincipal;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock private ClientRepository clientRepository;

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository);
    }

    @Test
    void listClientsAsAdminReturnsAllOrgClients() {
        UUID orgId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), orgId, "ADMIN", "admin@law.com");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> emptyPage = new PageImpl<>(Collections.emptyList());

        when(clientRepository.findByOrgIdAndDeletedAtIsNull(orgId, pageable)).thenReturn(emptyPage);

        Page<ClientResponseDto> result = clientService.listClients(orgId, principal, pageable);

        assertNotNull(result);
        verify(clientRepository, times(1)).findByOrgIdAndDeletedAtIsNull(orgId, pageable);
    }

    @Test
    void listClientsAsLawyerReturnsVisibleClients() {
        UUID orgId = UUID.randomUUID();
        UUID lawyerId = UUID.randomUUID();
        AuthPrincipal principal = new AuthPrincipal(lawyerId, orgId, "LAWYER", "lawyer@law.com");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Client> emptyPage = new PageImpl<>(Collections.emptyList());

        when(clientRepository.findVisibleForLawyer(orgId, lawyerId, pageable)).thenReturn(emptyPage);

        clientService.listClients(orgId, principal, pageable);

        verify(clientRepository, times(1)).findVisibleForLawyer(orgId, lawyerId, pageable);
    }
}

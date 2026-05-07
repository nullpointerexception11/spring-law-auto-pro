package com.lawauto.backend.client;

import com.lawauto.backend.auth.AuthPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Page<ClientResponseDto> listClients(UUID orgId, AuthPrincipal principal, Pageable pageable) {
        Page<Client> clientsPage;

        switch (principal.role()) {
            case "ADMIN":
                clientsPage = clientRepository.findByOrgIdAndDeletedAtIsNull(orgId, pageable);
                break;
            case "LAWYER":
                clientsPage = clientRepository.findVisibleForLawyer(orgId, principal.userId(), pageable);
                break;
            case "SECRETARY":
                clientsPage = clientRepository.findVisibleForSecretary(orgId, principal.userId(), pageable);
                break;
            default:
                throw new AccessDeniedException("Forbidden: unsupported role");
        }

        return clientsPage.map(ClientResponseDto::fromEntity);
    }
}

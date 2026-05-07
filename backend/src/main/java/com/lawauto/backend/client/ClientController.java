package com.lawauto.backend.client;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import com.lawauto.backend.common.PageMeta;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientService clientService;
    private final AuthorizationGuard authorizationGuard;

    public ClientController(ClientService clientService, AuthorizationGuard authorizationGuard) {
        this.clientService = clientService;
        this.authorizationGuard = authorizationGuard;
    }

    @GetMapping
    public ApiResponse<List<ClientResponseDto>> list(
            @RequestParam UUID orgId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        authorizationGuard.requireOrg(orgId);
        AuthPrincipal principal = authorizationGuard.currentPrincipal();

        Page<ClientResponseDto> clientsPage = clientService.listClients(orgId, principal, pageable);

        String sortString = pageable.getSort().isSorted() 
                ? pageable.getSort().iterator().next().getProperty() + "," + pageable.getSort().iterator().next().getDirection().name().toLowerCase()
                : "createdAt,desc";

        PageMeta meta = new PageMeta(
                clientsPage.getNumber(),
                clientsPage.getSize(),
                clientsPage.getTotalElements(),
                sortString
        );

        return ApiResponse.ok(clientsPage.getContent(), meta);
    }
}

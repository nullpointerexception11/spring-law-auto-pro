package com.lawauto.backend.petition;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.common.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/admin/petition-templates")
public class PetitionTemplateController {
    private final AuthorizationGuard authorizationGuard;
    private final PetitionTemplateService service;

    public PetitionTemplateController(AuthorizationGuard authorizationGuard, PetitionTemplateService service) {
        this.authorizationGuard = authorizationGuard;
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<PetitionTemplateDto>> list(@RequestParam UUID orgId) {
        authorizationGuard.requireOrg(orgId);
        authorizationGuard.requireRole("ADMIN");
        return ApiResponse.ok(service.listByOrg(orgId));
    }

    @PostMapping
    public Map<String, UUID> create(@Valid @RequestBody CreatePetitionTemplateRequest req) {
        authorizationGuard.requireOrg(req.orgId());
        authorizationGuard.requireRole("ADMIN");
        AuthPrincipal principal = authorizationGuard.currentPrincipal();
        return Map.of("id", service.create(principal, req));
    }

    @PatchMapping("/{id}")
    public ApiResponse<String> update(
            @RequestParam UUID orgId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePetitionTemplateRequest req
    ) {
        authorizationGuard.requireOrg(orgId);
        authorizationGuard.requireRole("ADMIN");
        service.update(orgId, id, req);
        return ApiResponse.ok("template-updated");
    }

    @PostMapping("/{id}/activate")
    public ApiResponse<String> activate(@RequestParam UUID orgId, @PathVariable UUID id) {
        authorizationGuard.requireOrg(orgId);
        authorizationGuard.requireRole("ADMIN");
        service.activate(orgId, id);
        return ApiResponse.ok("template-activated");
    }

    public record CreatePetitionTemplateRequest(
            @NotNull UUID orgId,
            @NotBlank String name,
            Integer version,
            Boolean isActive,
            @NotBlank String structureJson
    ) {}

    public record UpdatePetitionTemplateRequest(
            String name,
            Integer version,
            String structureJson
    ) {}
}

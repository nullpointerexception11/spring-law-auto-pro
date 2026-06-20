package com.lawauto.backend.ai.tools;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.matter.MatterService;
import com.lawauto.backend.matter.dto.CreateMatterRequest;
import org.springframework.security.core.context.SecurityContextHolder;
// @Component kaldirildi - Bean yonetimi AiConfigV2 uzerinden

import java.time.OffsetDateTime;
import java.util.UUID;

public class MatterTools {

    private final MatterService matterService;

    // Bu record mutlaka burada olmalı ✅
    public record MatterRequest(
            String title,
            String referenceNumber,
            String summary,
            String description) {
    }

    public MatterTools(MatterService matterService) {
        this.matterService = matterService;
    }

    /**
     * AI tarafından çağrılan metot.
     * Parametre olarak MatterRequest record'u alır.
     */
    public String createMatter(MatterRequest request) {
        AuthPrincipal principal = (AuthPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        UUID orgId = principal.orgId();

        CreateMatterRequest serviceRequest = new CreateMatterRequest(
                request.title(),
                request.referenceNumber(),
                request.summary(),
                request.description(),
                null,
                OffsetDateTime.now());

        UUID matterId = java.util.Objects.requireNonNull(matterService.createMatter(orgId, serviceRequest));

        return "Başarılı: '" + request.title() + "' başlıklı dava oluşturuldu. ID: " + matterId;
    }
}

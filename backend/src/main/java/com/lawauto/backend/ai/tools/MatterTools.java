package com.lawauto.backend.ai.tools;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.matter.MatterService;
import com.lawauto.backend.matter.dto.CreateMatterRequest;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * AI Tool for Matter (Dava) operations.
 * This class provides functions that the AI Agent can invoke.
 */
@Component
public class MatterTools {

    private final MatterService matterService;

    public MatterTools(MatterService matterService) {
        this.matterService = matterService;
    }

    /**
     * Creates a new legal matter (dava) in the system.
     * @param title The title of the matter (required).
     * @param referenceNumber The case number or reference (optional).
     * @param summary A short summary (optional).
     * @param description Full description (optional).
     * @return A success message with the created Matter ID.
     */
    @Tool(description = "Sistemde yeni bir hukuk davası (matter) oluşturur.")
    public String createMatter(String title, String referenceNumber, String summary, String description) {
        // 1. Get current organization from Security Context
        AuthPrincipal principal = (AuthPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        
        UUID orgId = principal.orgId();

        // 2. Prepare request
        CreateMatterRequest request = new CreateMatterRequest(
                title,
                referenceNumber,
                summary,
                description,
                null, // tags
                OffsetDateTime.now()
        );

        // 3. Invoke service
        UUID matterId = matterService.createMatter(orgId, request);

        return "Başarılı: '" + title + "' başlıklı dava oluşturuldu. Sistem ID: " + matterId;
    }
}

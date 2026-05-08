package com.lawauto.backend.cases;

import com.lawauto.backend.client.PartyRole;
import lombok.Data;

import java.util.UUID;

@Data
public class MatterRequest {
    private String title;
    private MatterType type;
    private String referenceNumber;
    private String descriptionHtml;
    
    // Initial Workflow context
    private UUID primaryPartyId;
    private PartyRole primaryPartyRole;
}

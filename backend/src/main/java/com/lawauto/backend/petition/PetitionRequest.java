package com.lawauto.backend.petition;

import lombok.Data;
import java.util.UUID;

@Data
public class PetitionRequest {
    private UUID matterId;
    private String title;
    private String bodyHtml;
}

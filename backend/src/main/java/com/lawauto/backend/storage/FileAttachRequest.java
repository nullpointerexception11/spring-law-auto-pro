package com.lawauto.backend.storage;

import lombok.Data;
import java.util.UUID;

@Data
public class FileAttachRequest {
    private String fileName;
    private String storageKey;
    private String mimeType;
    private Long sizeBytes;
    
    private UUID folderId;
    private String entityType; // MATTER, PETITION, etc.
    private UUID entityId;
    private String label;
}

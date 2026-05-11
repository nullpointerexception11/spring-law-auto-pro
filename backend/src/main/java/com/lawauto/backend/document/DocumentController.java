package com.lawauto.backend.document;

import com.lawauto.backend.auth.AuthorizationGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final AuthorizationGuard authorizationGuard;

    @PostMapping("/upload")
    public FileMetadata uploadDocument(
            @RequestParam("matterId") UUID matterId,
            @RequestParam("file") MultipartFile file) throws IOException {
        var principal = java.util.Objects.requireNonNull(authorizationGuard.currentPrincipal());
        return documentService.uploadDocument(principal.orgId(), matterId, file);
    }

    @GetMapping("/matter/{matterId}")
    public List<FileMetadata> listDocuments(@PathVariable UUID matterId) {
        var principal = java.util.Objects.requireNonNull(authorizationGuard.currentPrincipal());
        // In a real app, we should also verify access to the matter here
        return documentService.listDocumentsForMatter(principal.orgId(), matterId);
    }
}

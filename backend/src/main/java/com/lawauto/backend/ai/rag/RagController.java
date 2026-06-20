package com.lawauto.backend.ai.rag;

import com.lawauto.backend.ai.rag.dto.SearchResultDto;
import com.lawauto.backend.auth.AuthPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RAG (Retrieval-Augmented Generation) Controller.
 * 
 * Hukuki kaynakları indeksleme ve arama işlemleri için REST API.
 * 
 * Endpoints:
 * - POST /api/rag/search - Hybrid search
 * - POST /api/rag/index - Yeni doküman indeksleme
 * - DELETE /api/rag/clear - Org indeksini temizle
 */
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private static final Logger log = LoggerFactory.getLogger(RagController.class);
    
    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * Hybrid Search: Anlam + Anahtar Kelime araması.
     */
    @PostMapping("/search")
    public ResponseEntity<List<SearchResultDto>> search(@RequestBody SearchRequest request) {
        UUID orgId = getCurrentOrgId();
        if (orgId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        int limit = request.limit() > 0 ? Math.min(request.limit(), 20) : 5;
        List<SearchResultDto> results = ragService.hybridSearch(orgId, request.query(), limit);
        
        log.info("🔍 RAG arama: '{}' -> {} sonuç", request.query(), results.size());
        return ResponseEntity.ok(results);
    }

    /**
     * Sadece vektör (anlam) araması.
     */
    @PostMapping("/search/semantic")
    public ResponseEntity<List<SearchResultDto>> semanticSearch(@RequestBody SearchRequest request) {
        UUID orgId = getCurrentOrgId();
        if (orgId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(
            ragService.vectorSearch(orgId, request.query(), request.limit())
        );
    }

    /**
     * Sadece anahtar kelime araması.
     */
    @PostMapping("/search/keyword")
    public ResponseEntity<List<SearchResultDto>> keywordSearch(@RequestBody SearchRequest request) {
        UUID orgId = getCurrentOrgId();
        if (orgId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(
            ragService.keywordSearch(orgId, request.query(), request.limit())
        );
    }

    /**
     * Yeni bir hukuki dokümanı indeksle.
     * 
     * Örnek request:
     * {
     *   "sourceType": "KANUN",
     *   "sourceName": "Türk Borçlar Kanunu",
     *   "sourceReference": "TBK m.49",
     *   "fullText": "Madde 49 - Kusurlu ve hukuka aykırı..."
     * }
     */
    @PostMapping("/index")
    public ResponseEntity<Map<String, String>> indexDocument(@RequestBody IndexRequest request) {
        UUID orgId = getCurrentOrgId();
        if (orgId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        if (request.fullText() == null || request.fullText().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "fullText alanı zorunludur"));
        }
        if (request.sourceName() == null || request.sourceName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "sourceName alanı zorunludur"));
        }
        
        ragService.indexDocument(
            orgId,
            request.sourceType() != null ? request.sourceType() : "GENEL",
            request.sourceName(),
            request.sourceReference(),
            request.sourceDocumentId(),
            request.fullText()
        );
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "'" + request.sourceName() + "' başarıyla indekslendi"
        ));
    }

    /**
     * Organizasyonun tüm indeksini temizle.
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clearIndex() {
        UUID orgId = getCurrentOrgId();
        if (orgId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        ragService.clearOrgIndex(orgId);
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Tüm indeks temizlendi"
        ));
    }

    private UUID getCurrentOrgId() {
        try {
            AuthPrincipal principal = (AuthPrincipal) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            return principal.orgId();
        } catch (Exception e) {
            return null;
        }
    }

    // ========================
    // REQUEST RECORDS
    // ========================

    public record SearchRequest(String query, int limit) {}

    public record IndexRequest(
        String sourceType,
        String sourceName,
        String sourceReference,
        UUID sourceDocumentId,
        String fullText
    ) {}
}

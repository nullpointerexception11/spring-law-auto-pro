package com.lawauto.backend.ai.tools;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.lawauto.backend.ai.rag.RagService;
import com.lawauto.backend.ai.rag.dto.SearchResultDto;
import com.lawauto.backend.auth.AuthPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
// @Component kaldirildi - Bean yonetimi AiConfigV2 uzerinden yapiliyor

import java.util.List;
import java.util.UUID;

public class SearchLawTool {

    private static final Logger log = LoggerFactory.getLogger(SearchLawTool.class);

    private final RagService ragService;

    public SearchLawTool(RagService ragService) {
        this.ragService = ragService;
    }

    public String searchLaw(SearchLawRequest request) {
        AuthPrincipal principal = (AuthPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        UUID orgId = principal.orgId();

        log.info("Hukuki arama: sorgu='{}', kaynakTipi='{}', limit={}",
                request.query(), request.sourceType(), request.limit());

        List<SearchResultDto> results = ragService.hybridSearch(
                orgId,
                request.query(),
                request.limit() > 0 ? request.limit() : 5);

        if (results.isEmpty()) {
            return "Aramanizla ilgili hukuki kaynak bulunamadi. Lutfen farkli terimlerle tekrar deneyin.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Hukuki kaynak taramasi sonuclari:\n\n");

        for (int i = 0; i < results.size(); i++) {
            SearchResultDto r = results.get(i);
            sb.append("[").append(i + 1).append("] ").append(r.sourceName());
            if (r.sourceReference() != null && !r.sourceReference().isBlank()) {
                sb.append(" (").append(r.sourceReference()).append(")");
            }
            sb.append("\n");
            sb.append("   Tur: ").append(r.sourceType()).append("\n");
            sb.append("   Alaka duzeyi: %").append(String.format("%.1f", r.score() * 100)).append("\n");
            sb.append("   Icerik: ").append(r.content()).append("\n\n");
        }

        sb.append("Yukaridaki hukuki kaynaklari kullanarak kullaniciya cevap ver. ");
        sb.append("Kaynaklari atif yaparak kullan ve halusinasyon yapma.");

        return sb.toString();
    }

    public record SearchLawRequest(
            @JsonPropertyDescription("Aranacak hukuki terim, soru veya konu. Orn: 'icra takibinde zamanasimi suresi', 'is kazasi tazminat hesaplamasi'") String query,

            @JsonPropertyDescription("Kaynak turu filtresi. Bos birakilirsa tum kaynaklarda arar. Degerler: KANUN, YONETMELIK, ICTIHAT, GENELGE, MAKALE") String sourceType,

            @JsonPropertyDescription("Kac sonuc donecegi. Maksimum 10, varsayilan 5.") int limit) {
    }
}

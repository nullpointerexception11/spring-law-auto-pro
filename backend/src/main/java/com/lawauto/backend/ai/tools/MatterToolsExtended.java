package com.lawauto.backend.ai.tools;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// @Component kaldirildi - Bean yonetimi AiConfigV2 uzerinden

import java.util.List;
import java.util.UUID;

public class MatterToolsExtended {

    private static final Logger log = LoggerFactory.getLogger(MatterToolsExtended.class);

    public String createMatterDraft(CreateMatterDraftRequest request) {
        log.info("Dava taslagi olusturuluyor: {}", request.title());

        String draftId = UUID.randomUUID().toString().substring(0, 8);

        StringBuilder sb = new StringBuilder();
        sb.append("Dava taslagi basariyla olusturuldu!\n\n");
        sb.append("Taslak ID: ").append(draftId).append("\n");
        sb.append("Baslik: ").append(request.title()).append("\n");

        if (request.referenceNumber() != null && !request.referenceNumber().isBlank()) {
            sb.append("Referans No: ").append(request.referenceNumber()).append("\n");
        }

        if (request.summary() != null && !request.summary().isBlank()) {
            sb.append("Ozet: ").append(request.summary()).append("\n");
        }

        if (request.tags() != null && !request.tags().isEmpty()) {
            sb.append("Etiketler: ").append(String.join(", ", request.tags())).append("\n");
        }

        sb.append("\n---\n");
        sb.append("UYARI: Bu bir TASLAKTIR. Kaydedilmesi icin UI uzerinden onaylayin.");
        sb.append("\n\nOnaylamak icin ilgili taslagi bulun ve 'Kaydet' butonuna tiklayin.");

        return sb.toString();
    }

    public String summarizeMatter(SummarizeMatterRequest request) {
        return """
                Dava Ozeti

                Baslik: %s

                Bu dava ile ilgili kisa bir ozet hazirlandi. Detayli bilgi icin dava dosyasini inceleyebilirsiniz.

                *AI tarafindan olusturulmustur, lutfen dogrulayin.*
                """.formatted(request.matterId());
    }

    public String identifyParties(IdentifyPartiesRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dava Taraflari Belirleme\n\n");
        sb.append("Davaci: ").append(request.plaintiff()).append("\n");
        sb.append("Davali: ").append(request.defendant()).append("\n");

        if (request.additionalParties() != null && !request.additionalParties().isEmpty()) {
            sb.append("Diger Taraflar:\n");
            for (String party : request.additionalParties()) {
                sb.append("  - ").append(party).append("\n");
            }
        }

        sb.append("\n---\n");
        sb.append("UYARI: Bu bilgiler AI tarafindan belirlenmistir. Lutfen dogrulayin.");

        return sb.toString();
    }

    public record CreateMatterDraftRequest(
            @JsonPropertyDescription("Dava basligi (zorunlu). Orn: 'Is Kazasi Tazminat Davasi'") String title,

            @JsonPropertyDescription("Esas/referans numarasi. Daha acilmamissa bos birakilabilir.") String referenceNumber,

            @JsonPropertyDescription("Davanin kisa ozeti. Ne ile ilgili oldugunu aciklayin.") String summary,

            @JsonPropertyDescription("Dava ile ilgili etiketler. Orn: ['is-hukuku', 'tazminat']") List<String> tags) {
    }

    public record SummarizeMatterRequest(
            @JsonPropertyDescription("Ozetlenecek davanin ID'si") String matterId,

            @JsonPropertyDescription("Ozetin detay seviyesi: KISA, NORMAL, DETAYLI") String detailLevel) {
    }

    public record IdentifyPartiesRequest(
            @JsonPropertyDescription("Davacinin adi/unvani") String plaintiff,

            @JsonPropertyDescription("Davalinin adi/unvani") String defendant,

            @JsonPropertyDescription("Davaya dahil olabilecek diger taraflar") List<String> additionalParties) {
    }
}

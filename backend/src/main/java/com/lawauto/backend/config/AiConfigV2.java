package com.lawauto.backend.config;

import com.lawauto.backend.ai.rag.RagService;
import com.lawauto.backend.ai.routing.LargeModel;
import com.lawauto.backend.ai.routing.SmallModel;
import com.lawauto.backend.ai.tools.MatterTools;
import com.lawauto.backend.ai.tools.MatterToolsExtended;
import com.lawauto.backend.ai.tools.SearchLawTool;
import com.lawauto.backend.matter.MatterService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * GELİŞMİŞ AI YAPILANDIRMASI.
 * 
 * 4 ana prensibi içerir:
 * 1. RAG için pgvector + hybrid search
 * 2. Tool Calling (SearchLawTool, MatterTools)
 * 3. Model Routing (GPT-4o-mini / GPT-4o)
 * 4. Güvenlik (Data Masking, Human-in-the-Loop)
 */
@Configuration
public class AiConfigV2 {

    // ========================
    // 1. CHAT CLIENT BEAN'LERİ
    // ========================

    /**
     * Küçük Model (GPT-4o-mini) - Günlük işlemler için.
     * Varsayılan olarak kullanılır.
     */
    @Bean
    @Primary
    public ChatClient smallModelChatClient(
            ChatClient.Builder builder,
            @SmallModel MessageChatMemoryAdvisor smallMemoryAdvisor,
            SearchLawTool searchLawToolBean,
            MatterToolsExtended matterToolsExtendedBean) {

        return builder
                .defaultSystem("""
                        Sen profesyonel bir hukuk asistanısın (Law Auto AI).
                        Halüsinasyon yapma, sadece sana verilen bağlamla cevap ver.
                        Kısa ve net ol, gereksiz konuşma.
                        """)
                .defaultAdvisors(smallMemoryAdvisor)
                .defaultFunctions(
                        "searchLawTool",
                        "createMatterDraft",
                        "summarizeMatter",
                        "identifyParties")
                .build();
    }

    /**
     * Büyük Model (GPT-4o) - Karmaşık işlemler için.
     * Dilekçe yazımı, karmaşık analiz.
     */
    @Bean
    @LargeModel
    public ChatClient largeModelChatClient(
            ChatClient.Builder builder,
            @LargeModel MessageChatMemoryAdvisor largeMemoryAdvisor,
            SearchLawTool searchLawToolBean) {

        return builder
                .defaultSystem("""
                        Sen deneyimli bir hukuk bürosu uzman asistanısın.
                        Karmaşık hukuki analizler yap, detaylı dilekçeler yaz.
                        Her zaman kanun maddelerine atıf yap.
                        Halüsinasyon yapma, sadece verilen kaynakları kullan.
                        """)
                .defaultAdvisors(largeMemoryAdvisor)
                .defaultFunctions("searchLawTool")
                .build();
    }

    // ========================
    // 1.5. TOOL SINIFLARI BEAN'LERİ (@Component kaldirildigi icin)
    // ========================

    @Bean
    public SearchLawTool searchLawToolBean(RagService ragService) {
        return new SearchLawTool(ragService);
    }

    @Bean
    public MatterTools matterToolsBean(MatterService matterService) {
        return new MatterTools(matterService);
    }

    @Bean
    public MatterToolsExtended matterToolsExtendedBean() {
        return new MatterToolsExtended();
    }

    // ========================
    // 2. TOOL CALLBACK BEAN'LERİ
    // ========================

    /**
     * Hukuki Kaynak Arama Tool'u.
     * AI mevzuat/içtihat için bunu kullanır.
     */
    @Bean
    public FunctionCallbackWrapper<SearchLawTool.SearchLawRequest, String> searchLawTool(
            SearchLawTool searchLawToolBean) {
        return FunctionCallbackWrapper.builder(searchLawToolBean::searchLaw)
                .withName("searchLawTool")
                .withDescription("""
                        Hukuki kaynaklarda (kanun, yönetmelik, içtihat) anlamsal arama yapar.
                        Kullanıcının hukuki sorusuna cevap bulmak için önce bu tool'u çağır.
                        """)
                .withInputType(SearchLawTool.SearchLawRequest.class)
                .build();
    }

    /**
     * Dava Taslağı Oluşturma Tool'u (Human-in-the-Loop).
     * AI sadece taslak oluşturabilir, direk kayıt yapamaz.
     */
    @Bean
    public FunctionCallbackWrapper<MatterToolsExtended.CreateMatterDraftRequest, String> createMatterDraft(
            MatterToolsExtended matterToolsExtendedBean) {
        return FunctionCallbackWrapper.builder(matterToolsExtendedBean::createMatterDraft)
                .withName("createMatterDraft")
                .withDescription("""
                        Yeni bir hukuk davası (matter) taslağı oluşturur.
                        ÖNEMLİ: Bu sadece taslak oluşturur, kaydetmez.
                        Kullanıcı UI üzerinden onayladıktan sonra kaydedilir.
                        """)
                .withInputType(MatterToolsExtended.CreateMatterDraftRequest.class)
                .build();
    }

    /**
     * Dava Özetleme Tool'u.
     */
    @Bean
    public FunctionCallbackWrapper<MatterToolsExtended.SummarizeMatterRequest, String> summarizeMatter(
            MatterToolsExtended matterToolsExtendedBean) {
        return FunctionCallbackWrapper.builder(matterToolsExtendedBean::summarizeMatter)
                .withName("summarizeMatter")
                .withDescription("Mevcut bir davanın özetini çıkarır.")
                .withInputType(MatterToolsExtended.SummarizeMatterRequest.class)
                .build();
    }

    /**
     * Tarafları Belirleme Tool'u.
     */
    @Bean
    public FunctionCallbackWrapper<MatterToolsExtended.IdentifyPartiesRequest, String> identifyParties(
            MatterToolsExtended matterToolsExtendedBean) {
        return FunctionCallbackWrapper.builder(matterToolsExtendedBean::identifyParties)
                .withName("identifyParties")
                .withDescription("Davacı ve davalı taraflarını belirler.")
                .withInputType(MatterToolsExtended.IdentifyPartiesRequest.class)
                .build();
    }

    /**
     * Legacy Tool (geriye uyumluluk).
     */
    @Bean
    public FunctionCallbackWrapper<MatterTools.MatterRequest, String> createMatterTool(MatterTools matterToolsBean) {
        return FunctionCallbackWrapper.builder(matterToolsBean::createMatter)
                .withName("createMatter")
                .withDescription(
                        "Sistemde yeni bir hukuk davası (matter) oluşturur. Tercihen createMatterDraft kullanın.")
                .withInputType(MatterTools.MatterRequest.class)
                .build();
    }
}

package com.lawauto.backend.ai;

import com.lawauto.backend.ai.prompt.SystemPrompts;
import com.lawauto.backend.ai.rag.RagService;
import com.lawauto.backend.ai.routing.ModelRouter;
import com.lawauto.backend.ai.security.DataMaskingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.UUID;

/**
 * GELİŞMİŞ AI ASİSTAN CONTROLLER
 * 
 * 4 ana prensibi birleştirir:
 * 1. RAG (Retrieval-Augmented Generation)
 * 2. Tool Calling & Agent
 * 3. Model Routing & Maliyet Optimizasyonu
 * 4. Güvenlik & Data Masking
 * 
 * İŞ AKIŞI:
 * Kullanıcı mesajı -> Data Masking -> Model Routing (Mini/Large) -> 
 * RAG Context Injection -> Tool Calling -> Cevap
 */
@RestController
@RequestMapping("/api/ai/v2")
public class AiAssistantControllerV2 {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantControllerV2.class);

    private final ChatClient chatClient;
    private final RagService ragService;
    private final ModelRouter modelRouter;
    private final DataMaskingService dataMaskingService;

    public AiAssistantControllerV2(
            ChatClient chatClient,
            RagService ragService,
            ModelRouter modelRouter,
            DataMaskingService dataMaskingService) {
        this.chatClient = chatClient;
        this.ragService = ragService;
        this.modelRouter = modelRouter;
        this.dataMaskingService = dataMaskingService;
    }

    /**
     * Standart Chat (RAG + Model Routing ile)
     * 
     * Akış:
     * 1. Hassas verileri maskele
     * 2. İşlem türünü sınıflandır (dilekçe yazımı mı, basit soru mu?)
     * 3. RAG ile hukuki bağlam topla
     * 4. Uygun modeli seç (Mini vs Large)
     * 5. Cevabı döndür
     */
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        // 1. GÜVENLİK: Hassas verileri maskele
        String maskedMessage = dataMaskingService.prepareForAi(
            request.message(), 
            DataMaskingService.MaskLevel.STANDARD
        );

        // 2. İşlem türünü sınıflandır (Model Routing)
        ModelRouter.TaskType taskType = modelRouter.classifyTask(maskedMessage);
        log.info("🔄 İşlem türü: {}, mesaj: {}", taskType, maskedMessage.substring(0, Math.min(50, maskedMessage.length())));

        // 3. RAG: Hukuki bağlam topla (tool çağrısı değilse)
        String ragContext = "";
        if (taskType != ModelRouter.TaskType.TOOL_CAGRISI) {
            try {
                UUID orgId = getCurrentOrgId();
                if (orgId != null) {
                    ragContext = ragService.buildContextForAi(orgId, maskedMessage);
                }
            } catch (Exception e) {
                log.warn("RAG bağlamı alınamadı: {}", e.getMessage());
            }
        }

        // 4. System prompt'u seç
        String systemPrompt = switch (taskType) {
            case DILEKCE_YAZIMI -> SystemPrompts.DILEKCE_YAZIMI;
            case TOOL_CAGRISI -> SystemPrompts.TOOL_KULLANIMI;
            default -> SystemPrompts.forRole("Genel hukuk danışmanı");
        };

        // RAG bağlamını ekle (varsa)
        if (!ragContext.isEmpty()) {
            systemPrompt = ragContext + "\n\n" + SystemPrompts.RAG_CONTEXT_PROMPT;
        }

        // 5. Model Routing: Uygun model ile işle
        String answer;
        if (taskType == ModelRouter.TaskType.DILEKCE_YAZIMI || 
            taskType == ModelRouter.TaskType.KARMASIK_ANALIZ ||
            taskType == ModelRouter.TaskType.SOZLESME_DEGERLENDIRME) {
            // Büyük model kullan
            answer = modelRouter.execute(taskType, maskedMessage, systemPrompt);
        } else {
            // Küçük model + Tool Calling ile
            answer = chatClient.prompt()
                    .system(systemPrompt)
                    .user(maskedMessage)
                    .advisors(a -> a.param("chat_memory_id", request.conversationId()))
                    .call()
                    .content();
        }

        // 6. Cevabı temizle ve döndür
        String cleanAnswer = dataMaskingService.unmaskForDisplay(answer);
        
        return new ChatResponse(
            cleanAnswer,
            taskType.name(),
            !ragContext.isEmpty()
        );
    }

    /**
     * Stream Chat (RAG + Model Routing ile)
     */
    @PostMapping("/chat/stream")
    public Flux<String> chatStream(@RequestBody ChatRequest request) {
        String maskedMessage = dataMaskingService.prepareForAi(
            request.message(), 
            DataMaskingService.MaskLevel.STANDARD
        );
        
        ModelRouter.TaskType taskType = modelRouter.classifyTask(maskedMessage);
        
        String ragContext = "";
        if (taskType != ModelRouter.TaskType.TOOL_CAGRISI) {
            try {
                UUID orgId = getCurrentOrgId();
                if (orgId != null) {
                    ragContext = ragService.buildContextForAi(orgId, maskedMessage);
                }
            } catch (Exception e) {
                log.warn("RAG bağlamı alınamadı: {}", e.getMessage());
            }
        }

        String systemPrompt = ragContext.isEmpty() 
            ? SystemPrompts.forRole("Genel hukuk danışmanı")
            : ragContext + "\n\n" + SystemPrompts.RAG_CONTEXT_PROMPT;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(maskedMessage)
                .advisors(a -> a.param("chat_memory_id", request.conversationId()))
                .stream()
                .content();
    }

    /**
     * Sadece RAG araması yap (AI yanıtı olmadan).
     */
    @PostMapping("/search")
    public java.util.List<com.lawauto.backend.ai.rag.dto.SearchResultDto> search(
            @RequestBody SearchRequest request) {
        UUID orgId = getCurrentOrgId();
        if (orgId == null) {
            return java.util.Collections.emptyList();
        }
        return ragService.hybridSearch(orgId, request.query(), request.limit());
    }

    /**
     * Mevcut kullanıcının org ID'sini al.
     */
    private UUID getCurrentOrgId() {
        try {
            var principal = (com.lawauto.backend.auth.AuthPrincipal) 
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return principal.orgId();
        } catch (Exception e) {
            return null;
        }
    }

    // ========================
    // REQUEST / RESPONSE RECORDS
    // ========================

    public record ChatRequest(String message, String conversationId) {}

    public record ChatResponse(String message, String taskType, boolean hasRagContext) {}

    public record SearchRequest(String query, int limit) {}
}

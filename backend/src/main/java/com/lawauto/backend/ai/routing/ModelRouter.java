package com.lawauto.backend.ai.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class ModelRouter {

    private static final Logger log = LoggerFactory.getLogger(ModelRouter.class);
    
    private final ChatClient largeModelClient;
    private final ChatClient smallModelClient;

    public ModelRouter(
            @LargeModel ChatClient largeModelClient,
            @SmallModel ChatClient smallModelClient) {
        this.largeModelClient = largeModelClient;
        this.smallModelClient = smallModelClient;
    }

    public String execute(TaskType taskType, String prompt, String systemPrompt) {
        return switch (taskType) {
            case DILEKCE_YAZIMI -> {
                log.info("Dilekce yazimi -> Buyuk model (GPT-4o)");
                yield callWithFallback(largeModelClient, prompt, systemPrompt, taskType);
            }
            case KARMASIK_ANALIZ -> {
                log.info("Karmasik analiz -> Buyuk model (GPT-4o)");
                yield callWithFallback(largeModelClient, prompt, systemPrompt, taskType);
            }
            case SOZLESME_DEGERLENDIRME -> {
                log.info("Sozlesme degerlendirme -> Buyuk model (GPT-4o)");
                yield callWithFallback(largeModelClient, prompt, systemPrompt, taskType);
            }
            case SINIFLANDIRMA -> {
                log.info("Siniflandirma -> Kucuk model (GPT-4o-mini)");
                yield callModel(smallModelClient, prompt, systemPrompt);
            }
            case OZETLEME -> {
                log.info("Ozetleme -> Kucuk model (GPT-4o-mini)");
                yield callModel(smallModelClient, prompt, systemPrompt);
            }
            case TOOL_CAGRISI -> {
                log.info("Tool cagrisi -> Kucuk model (GPT-4o-mini)");
                yield callModel(smallModelClient, prompt, systemPrompt);
            }
            case BASIT_SORU_CEVAP -> {
                log.info("Basit soru-cevap -> Kucuk model (GPT-4o-mini)");
                yield callModel(smallModelClient, prompt, systemPrompt);
            }
            case ETIKETLEME -> {
                log.info("Etiketleme -> Kucuk model (GPT-4o-mini)");
                yield callModel(smallModelClient, prompt, systemPrompt);
            }
        };
    }

    private String callWithFallback(ChatClient client, String prompt, 
                                   String systemPrompt, TaskType taskType) {
        try {
            return callModel(client, prompt, systemPrompt);
        } catch (Exception e) {
            log.warn("Buyuk model basarisiz, kucuk modele dusuluyor: {}", e.getMessage());
            return callModel(smallModelClient, prompt, systemPrompt);
        }
    }

    private String callModel(ChatClient client, String prompt, String systemPrompt) {
        return client.prompt()
                .system(systemPrompt)
                .user(prompt)
                .call()
                .content();
    }

    public TaskType classifyTask(String userMessage) {
        String lower = userMessage.toLowerCase();
        
        if (lower.contains("dilekce") || lower.contains("dilekce yaz") || 
            lower.contains("sikayet dilekcesi") || lower.contains("dava dilekcesi") ||
            (lower.contains("itiraz") && lower.contains("dilekce"))) {
            return TaskType.DILEKCE_YAZIMI;
        }
        
        if (lower.contains("sozlesme") && 
            (lower.contains("degerlendir") || lower.contains("incele") || lower.contains("hazirla"))) {
            return TaskType.SOZLESME_DEGERLENDIRME;
        }
        
        if (lower.contains("analiz") || lower.contains("yorumla") || 
            lower.contains("karsilastir") || lower.contains("karsilastirma") ||
            (lower.contains("degerlendir") && lower.contains("olasilik"))) {
            return TaskType.KARMASIK_ANALIZ;
        }
        
        if (lower.contains("siniflandir") || lower.contains("kategori") || 
            lower.contains("tur") && lower.contains("nedir")) {
            return TaskType.SINIFLANDIRMA;
        }
        
        if (lower.contains("ozet") || lower.contains("kisaca") || 
            lower.contains("kisa aciklama") || lower.contains("ozetle")) {
            return TaskType.OZETLEME;
        }
        
        if (lower.contains("dava ac") || lower.contains("kaydet") || 
            lower.contains("olustur") || lower.contains("dava ekle") || 
            lower.contains("matter ekle") || lower.contains("taslak")) {
            return TaskType.TOOL_CAGRISI;
        }
        
        if (lower.contains("etiketle") || lower.contains("tag") || lower.contains("kategorize")) {
            return TaskType.ETIKETLEME;
        }
        
        return TaskType.BASIT_SORU_CEVAP;
    }

    public enum TaskType {
        DILEKCE_YAZIMI,
        KARMASIK_ANALIZ,
        SOZLESME_DEGERLENDIRME,
        SINIFLANDIRMA,
        OZETLEME,
        TOOL_CAGRISI,
        BASIT_SORU_CEVAP,
        ETIKETLEME
    }
}

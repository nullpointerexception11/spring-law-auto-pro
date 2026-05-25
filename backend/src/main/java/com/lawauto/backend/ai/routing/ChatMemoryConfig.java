package com.lawauto.backend.ai.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    private static final Logger log = LoggerFactory.getLogger(ChatMemoryConfig.class);

    @Bean
    public ChatMemory chatMemory() {
        log.info("InMemoryChatMemory baslatildi (mesaj gecmisi: son 5 mesaj)");
        return new InMemoryChatMemory();
    }

    @Bean
    @SmallModel
    public MessageChatMemoryAdvisor smallModelChatMemoryAdvisor(ChatMemory chatMemory) {
        // Spring AI 1.0.0-M2: (ChatMemory, String defaultConversationId, int
        // chatHistoryWindowSize)
        return new MessageChatMemoryAdvisor(chatMemory,
                "chat_memory_id",
                5);
    }

    @Bean
    @LargeModel
    public MessageChatMemoryAdvisor largeModelChatMemoryAdvisor(ChatMemory chatMemory) {
        return new MessageChatMemoryAdvisor(chatMemory,
                "chat_memory_id",
                10);
    }
}

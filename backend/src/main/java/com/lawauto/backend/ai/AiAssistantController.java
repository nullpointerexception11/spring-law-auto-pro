package com.lawauto.backend.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.web.bind.annotation.*;
import com.lawauto.backend.ai.tools.MatterTools;
import java.util.Objects;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    private final ChatClient chatClient;

    public AiAssistantController(ChatClient.Builder chatClientBuilder) {
        
        // Using Bean name registration which is the most stable across Milestone versions
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .defaultFunctions("createMatterTool") // We will define this as a @Bean
                .build();
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return Objects.requireNonNull(chatClient.prompt()
                .user(request.message())
                .advisors(a -> a.param("chat_memory_id", request.conversationId()))
                .call()
                .content());
    }

    @PostMapping("/chat/stream")
    public Flux<String> chatStream(@RequestBody ChatRequest request) {
        return chatClient.prompt()
                .user(request.message())
                .advisors(a -> a.param("chat_memory_id", request.conversationId()))
                .stream()
                .content();
    }

    public record ChatRequest(String message, String conversationId) {
    }
}

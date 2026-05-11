package com.lawauto.backend.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.web.bind.annotation.*;
import com.lawauto.backend.ai.tools.MatterTools;
import java.util.Objects;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    private final ChatClient chatClient;

    public AiAssistantController(ChatClient.Builder chatClientBuilder, 
                                MatterTools matterTools) {
        
        // defaultAdvisors worked, but for FunctionCallbackWrapper we try 'functions'
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .functions(FunctionCallbackWrapper.builder(matterTools::createMatter)
                        .withName("createMatter")
                        .withDescription("Sistemde yeni bir hukuk davası (matter) oluşturur.")
                        .withInputType(MatterTools.MatterRequest.class)
                        .build())
                .build();
    }

    @PostMapping("/chat")
    public String chat(@RequestBody ChatRequest request) {
        return Objects.requireNonNull(chatClient.prompt()
                .user(request.message())
                .call()
                .content());
    }

    @PostMapping("/chat/stream")
    public Flux<String> chatStream(@RequestBody ChatRequest request) {
        return chatClient.prompt()
                .user(request.message())
                .stream()
                .content();
    }

    public record ChatRequest(String message) {
    }
}

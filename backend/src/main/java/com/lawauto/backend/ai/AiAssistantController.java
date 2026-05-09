package com.lawauto.backend.ai;

import org.springaicommunity.agent.tools.SkillsTool;
import org.springaicommunity.agent.tools.FileSystemTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    private final ChatClient chatClient;

    public AiAssistantController(ChatClient.Builder chatClientBuilder, 
                                ResourceLoader resourceLoader,
                                com.lawauto.backend.ai.tools.MatterTools matterTools) {
        // We configure the ChatClient with Skills, FileSystem access, and our custom Java Tools.
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(SkillsTool.builder()
                        .addSkillsResource(resourceLoader.getResource("classpath:skills"))
                        .build())
                .defaultTools(FileSystemTools.builder().build())
                .defaultTools(matterTools) // Registering our Matter creation tool
                .build();
    }

    public record ChatRequest(String message) {
    }

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody ChatRequest request) {
        String response = chatClient.prompt()
                .user(request.message())
                .call()
                .content();

        return Map.of("reply", response);
    }
}

package com.lawauto.backend.config;

import com.lawauto.backend.ai.tools.MatterTools;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import java.util.function.Function;

@Configuration
public class AiConfig {

    @Bean
    public FunctionCallbackWrapper<MatterTools.MatterRequest, String> createMatterTool(MatterTools matterTools) {
        return FunctionCallbackWrapper.builder(matterTools::createMatter)
                .withName("createMatter")
                .withDescription("Sistemde yeni bir hukuk davası (matter) oluşturur.")
                .withInputType(MatterTools.MatterRequest.class)
                .build();
    }
}

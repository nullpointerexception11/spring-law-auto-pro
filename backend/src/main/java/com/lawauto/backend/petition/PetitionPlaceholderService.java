package com.lawauto.backend.petition;

import com.lawauto.backend.cases.MatterRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PetitionPlaceholderService {

    private final MatterRepository matterRepository;

    public PetitionPlaceholderService(MatterRepository matterRepository) {
        this.matterRepository = matterRepository;
    }

    /**
     * PRESTIGE LMMS: Fetch dynamic placeholders for a Matter.
     * Integrates with the Matter-Centric architecture.
     */
    @Cacheable(value = "petitionPlaceholders", key = "#matterId")
    public Map<String, String> getPlaceholders(UUID matterId) {
        java.util.Objects.requireNonNull(matterId);
        log.info("Fetching placeholders for matter [{}]", matterId);
        Map<String, String> placeholders = new HashMap<>();
        
        matterRepository.findById(matterId).ifPresent(matter -> {
            placeholders.put("matter_title", matter.getTitle());
            placeholders.put("matter_type", matter.getType().name());
            placeholders.put("reference_number", matter.getReferenceNumber());
            placeholders.put("status", matter.getStatus().name());
            placeholders.put("opened_at", matter.getOpenedAt().toString());
            
            // Future: Extract more placeholders from LitigationDetail or dataJson
        });

        return placeholders;
    }

    public String replace(String content, Map<String, String> placeholders) {
        if (content == null) return "";
        String result = content;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue();
            result = result.replace("{{" + entry.getKey() + "}}", value);
        }
        return result;
    }
}

package com.lawauto.backend.ai.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {

    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);

    private final EmbeddingModel embeddingModel;
    private final org.springframework.cache.Cache embeddingCache;
    private final org.springframework.cache.CacheManager cacheManager;

    public EmbeddingService(EmbeddingModel embeddingModel,
            org.springframework.cache.CacheManager cacheManager) {
        this.embeddingModel = embeddingModel;
        this.cacheManager = cacheManager;
        this.embeddingCache = cacheManager.getCache("embeddingCache");
    }

    public float[] generateEmbedding(String text) {
        if (embeddingCache != null) {
            String cacheKey = text.hashCode() + "_" + text.length();
            org.springframework.cache.Cache.ValueWrapper cached = embeddingCache.get(cacheKey);
            if (cached != null && cached.get() instanceof float[]) {
                return (float[]) cached.get();
            }
        }

        // Spring AI 1.0.0-M2: embed(String) returns float[]
        float[] embedding = embeddingModel.embed(text);

        if (embeddingCache != null) {
            String cacheKey = text.hashCode() + "_" + text.length();
            embeddingCache.put(cacheKey, embedding);
        }

        return embedding;
    }

    public List<float[]> generateEmbeddings(List<String> texts) {
        // Spring AI 1.0.0-M2: embed(List<String>) returns List<float[]>
        return embeddingModel.embed(texts);
    }

    public void enrichChunksWithEmbeddings(List<LegalChunk> chunks) {
        List<String> contents = chunks.stream()
                .map(LegalChunk::getContent)
                .toList();

        List<float[]> embeddings = generateEmbeddings(contents);

        for (int i = 0; i < chunks.size(); i++) {
            chunks.get(i).setEmbedding(embeddings.get(i));
        }

        log.info("{} chunk embedding ile zenginlestirildi", chunks.size());
    }

    public float[] generateQueryEmbedding(String query) {
        return generateEmbedding(query);
    }
}

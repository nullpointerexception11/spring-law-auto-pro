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
            String cacheKey = cacheKeyFor(text);
            org.springframework.cache.Cache.ValueWrapper cached = embeddingCache.get(cacheKey);
            if (cached != null && cached.get() instanceof float[]) {
                return (float[]) cached.get();
            }
        }

        // Spring AI 1.0.0-M2: embed(String) returns float[]
        float[] embedding = embeddingModel.embed(text);

        if (embeddingCache != null) {
            embeddingCache.put(cacheKeyFor(text), embedding);
        }

        return embedding;
    }

    /**
     * PERFORMANCE NOTE: the previous key (`text.hashCode() + "_" +
     * text.length()`) had two problems:
     *  1. Correctness: java.lang.String#hashCode() is a 32-bit hash with no
     *     uniqueness guarantee. Two different chunks of the same length
     *     CAN collide and silently return the wrong cached embedding to the
     *     vector search / RAG pipeline — a correctness bug disguised as a
     *     performance optimization.
     *  2. Hit-rate: raw, un-normalized text means "Hello world" and
     *     "hello world  " are treated as different keys even though an
     *     embedding model would likely benefit from being deduplicated.
     *
     * SHA-256 over a normalized (trimmed, whitespace-collapsed) string
     * fixes both: an effectively collision-free key, and a higher cache
     * hit rate on near-duplicate inputs (common with re-indexed or
     * re-chunked legal text).
     */
    private static String cacheKeyFor(String text) {
        String normalized = text.trim().replaceAll("\\s+", " ");
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(normalized.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed available on every JVM; this is unreachable.
            throw new IllegalStateException(e);
        }
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

package com.lawauto.backend.ai.rag;

import com.lawauto.backend.ai.rag.dto.SearchResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagService {

    private static final Logger log = LoggerFactory.getLogger(RagService.class);
    
    private final LegalChunkRepository legalChunkRepository;
    private final EmbeddingService embeddingService;
    private final ChunkingService chunkingService;

    public RagService(LegalChunkRepository legalChunkRepository,
                     EmbeddingService embeddingService,
                     ChunkingService chunkingService) {
        this.legalChunkRepository = legalChunkRepository;
        this.embeddingService = embeddingService;
        this.chunkingService = chunkingService;
    }

    @Transactional(readOnly = true)
    public List<SearchResultDto> hybridSearch(UUID orgId, String query, int limit) {
        float[] queryEmbedding = embeddingService.generateQueryEmbedding(query);
        List<Object[]> results = legalChunkRepository.hybridSearch(orgId, query, queryEmbedding, limit);

        List<SearchResultDto> dtos = new ArrayList<>();
        for (Object[] row : results) {
            SearchResultDto dto = new SearchResultDto(
                (UUID) row[0],
                (String) row[3],
                (String) row[4],
                (String) row[2],
                (String) row[7],
                (String) row[8],
                ((Number) row[9]).doubleValue()
            );
            dtos.add(dto);
        }

        log.info("Hybrid search: sorgu='{}' -> {} sonuc", query, dtos.size());
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<SearchResultDto> vectorSearch(UUID orgId, String query, int limit) {
        float[] queryEmbedding = embeddingService.generateQueryEmbedding(query);
        List<Object[]> results = legalChunkRepository.vectorSearch(orgId, queryEmbedding, limit);
        
        return results.stream().map(row -> new SearchResultDto(
            (UUID) row[0], (String) row[3], (String) row[4],
            (String) row[2], (String) row[7], (String) row[8],
            ((Number) row[9]).doubleValue()
        )).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SearchResultDto> keywordSearch(UUID orgId, String query, int limit) {
        List<Object[]> results = legalChunkRepository.fullTextSearch(orgId, query, limit);
        
        return results.stream().map(row -> new SearchResultDto(
            (UUID) row[0], (String) row[3], (String) row[4],
            (String) row[2], (String) row[7], (String) row[8],
            ((Number) row[9]).doubleValue()
        )).collect(Collectors.toList());
    }

    public String buildContextForAi(UUID orgId, String query) {
        List<SearchResultDto> searchResults = hybridSearch(orgId, query, 5);
        
        if (searchResults.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();
        context.append("=== HUKUKI KAYNAKLARDAN ALINAN BAGLAM ===\n\n");
        context.append("Asagidaki bilgiler yasal veritabanindan alinmistir. ");
        context.append("Sadece bu bilgileri kullanarak cevap ver.\n\n");

        for (int i = 0; i < searchResults.size(); i++) {
            SearchResultDto result = searchResults.get(i);
            context.append("--- Kaynak ").append(i + 1).append(" ---\n");
            context.append("Kaynak: ").append(result.sourceName()).append("\n");
            if (result.sourceReference() != null && !result.sourceReference().isBlank()) {
                context.append("Referans: ").append(result.sourceReference()).append("\n");
            }
            context.append("Tur: ").append(result.sourceType()).append("\n");
            context.append("Icerik: ").append(result.content()).append("\n\n");
        }
        
        context.append("=== BAGLAM SONU ===\n");
        context.append("ONEMLI: Yukaridaki bilgilerin disina cikma ve halusinasyon yapma. ");
        context.append("Eger yeterli bilgi yoksa, bunu kullaniciya soyle.\n");

        return context.toString();
    }

    @Transactional
    public void indexDocument(UUID orgId, String sourceType, String sourceName,
                             String sourceReference, UUID sourceDocumentId, 
                             String fullText) {
        List<LegalChunk> chunks = chunkingService.chunkDocument(
            orgId, sourceType, sourceName, sourceReference, sourceDocumentId, fullText
        );
        embeddingService.enrichChunksWithEmbeddings(chunks);
        legalChunkRepository.saveAll(chunks);
        log.info("Dokuman indekslendi: {} ({} chunk)", sourceName, chunks.size());
    }

    @Transactional
    public void clearOrgIndex(UUID orgId) {
        legalChunkRepository.deleteByOrgId(orgId);
        log.info("Org indeksi temizlendi: {}", orgId);
    }
}

package com.lawauto.backend.ai.rag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LegalChunkRepository extends JpaRepository<LegalChunk, UUID> {

    @Query(value = """
        SELECT id, org_id, source_type, source_name, source_reference, 
               source_document_id, chunk_index, content, metadata,
               1 - cosine_distance(embedding, :queryEmbedding::vector) AS similarity_score
        FROM legal_chunk
        WHERE org_id = :orgId
        ORDER BY embedding <-> :queryEmbedding::vector
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> vectorSearch(
        @Param("orgId") UUID orgId,
        @Param("queryEmbedding") float[] queryEmbedding,
        @Param("limit") int limit
    );

    @Query(value = """
        SELECT id, org_id, source_type, source_name, source_reference,
               source_document_id, chunk_index, content, metadata,
               ts_rank(to_tsvector('turkish', content), plainto_tsquery('turkish', :query)) AS relevance_score
        FROM legal_chunk
        WHERE org_id = :orgId
          AND to_tsvector('turkish', content) @@ plainto_tsquery('turkish', :query)
        ORDER BY relevance_score DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> fullTextSearch(
        @Param("orgId") UUID orgId,
        @Param("query") String query,
        @Param("limit") int limit
    );

    @Query(value = """
        WITH vector_results AS (
            SELECT id, 
                   1 - cosine_distance(embedding, :queryEmbedding::vector) AS score
            FROM legal_chunk
            WHERE org_id = :orgId
            ORDER BY embedding <-> :queryEmbedding::vector
            LIMIT :limit * 2
        ),
        text_results AS (
            SELECT id,
                   ts_rank(to_tsvector('turkish', content), plainto_tsquery('turkish', :query)) AS score
            FROM legal_chunk
            WHERE org_id = :orgId
              AND to_tsvector('turkish', content) @@ plainto_tsquery('turkish', :query)
            LIMIT :limit * 2
        )
        SELECT lc.id, lc.org_id, lc.source_type, lc.source_name, 
               lc.source_reference, lc.source_document_id, lc.chunk_index,
               lc.content, lc.metadata,
               (COALESCE(vr.score, 0) * 0.6 + COALESCE(tr.score, 0) * 0.4) AS hybrid_score
        FROM legal_chunk lc
        LEFT JOIN vector_results vr ON lc.id = vr.id
        LEFT JOIN text_results tr ON lc.id = tr.id
        WHERE lc.org_id = :orgId
          AND (vr.id IS NOT NULL OR tr.id IS NOT NULL)
        ORDER BY hybrid_score DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> hybridSearch(
        @Param("orgId") UUID orgId,
        @Param("query") String query,
        @Param("queryEmbedding") float[] queryEmbedding,
        @Param("limit") int limit
    );

    List<LegalChunk> findBySourceDocumentIdOrderByChunkIndex(UUID sourceDocumentId);
    void deleteByOrgId(UUID orgId);
}

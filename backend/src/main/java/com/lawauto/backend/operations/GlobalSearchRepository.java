package com.lawauto.backend.operations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface GlobalSearchRepository extends JpaRepository<SearchDocument, UUID> {

    /**
     * Blazing fast Global Search using PostgreSQL Native Full-Text Search.
     * Uses Turkish stemming and dictionary for accurate contextual matching across all entities.
     * Fallback to ILIKE for partial exact matches on the title.
     */
    @Query(value = """
        SELECT * FROM "SearchDocument"
        WHERE "orgId" = :orgId
        AND (
            to_tsvector('turkish', coalesce(title, '') || ' ' || coalesce(body, '')) @@ plainto_tsquery('turkish', :keyword)
            OR title ILIKE '%' || :keyword || '%'
        )
    """, countQuery = """
        SELECT count(*) FROM "SearchDocument"
        WHERE "orgId" = :orgId
        AND (
            to_tsvector('turkish', coalesce(title, '') || ' ' || coalesce(body, '')) @@ plainto_tsquery('turkish', :keyword)
            OR title ILIKE '%' || :keyword || '%'
        )
    """, nativeQuery = true)
    Page<SearchDocument> searchGlobally(@Param("orgId") UUID orgId, @Param("keyword") String keyword, Pageable pageable);
}

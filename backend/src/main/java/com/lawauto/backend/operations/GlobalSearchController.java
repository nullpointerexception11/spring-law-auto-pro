package com.lawauto.backend.operations;

import com.lawauto.backend.operations.dto.SearchResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/search")
public class GlobalSearchController {

    private final GlobalSearchService searchService;

    public GlobalSearchController(GlobalSearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * GET /api/search?orgId={orgId}&q={keyword}&page=0&size=20
     * Executes a lightning-fast Global Search across all entities.
     */
    @GetMapping
    public Page<SearchResultDto> search(
            @RequestParam UUID orgId,
            @RequestParam String q,
            Pageable pageable) {
        return searchService.searchGlobally(orgId, q, pageable);
    }
}

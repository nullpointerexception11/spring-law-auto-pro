package com.lawauto.backend.operations;

import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.operations.dto.SearchResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GlobalSearchService {

    private final GlobalSearchRepository searchRepository;
    private final AuthorizationGuard authorizationGuard;

    public GlobalSearchService(GlobalSearchRepository searchRepository, 
                               AuthorizationGuard authorizationGuard) {
        this.searchRepository = searchRepository;
        this.authorizationGuard = authorizationGuard;
    }

    /**
     * Executes the highly optimized Native Full-Text Search.
     * Enforces Tenant Isolation and maps massive DB entries to lightweight UI snippets.
     */
    @Transactional(readOnly = true)
    public Page<SearchResultDto> searchGlobally(UUID orgId, String keyword, Pageable pageable) {
        // Enforce strict Tenant isolation boundary
        authorizationGuard.requireOrg(orgId);
        
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        // Map native entities to UI-friendly DTOs on the fly
        return searchRepository.searchGlobally(orgId, keyword.trim(), pageable)
                .map(SearchResultDto::fromEntity);
    }
}

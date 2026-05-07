package com.lawauto.backend.org;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OrgService {
    private final OrgRepository orgRepository;

    public OrgService(OrgRepository orgRepository) {
        this.orgRepository = orgRepository;
    }

    public OrgResponseDto getOrg(UUID orgId) {
        Org org = orgRepository.findById(orgId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found"));
        return OrgResponseDto.fromEntity(org);
    }
}

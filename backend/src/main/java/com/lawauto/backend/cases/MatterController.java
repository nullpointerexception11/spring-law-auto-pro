package com.lawauto.backend.cases;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matters")
@RequiredArgsConstructor
public class MatterController {

    private final MatterService matterService;
    private final MatterRepository matterRepository;

    /**
     * WORKFLOW: Initiate a new Matter.
     */
    @PostMapping
    public ResponseEntity<Matter> initiateMatter(
            @RequestBody MatterRequest request,
            @RequestHeader("X-Org-Id") UUID orgId,
            @RequestHeader("X-User-Id") UUID userId) {
        
        Matter matter = matterService.initiateMatter(request, orgId, userId);
        return ResponseEntity.ok(matter);
    }

    /**
     * WORKFLOW: List all active matters for the organization.
     */
    @GetMapping
    public ResponseEntity<List<Matter>> getActiveMatters(@RequestHeader("X-Org-Id") UUID orgId) {
        return ResponseEntity.ok(matterRepository.findActiveMatters(orgId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Matter> getMatterById(
            @PathVariable UUID id,
            @RequestHeader("X-Org-Id") UUID orgId) {
        
        return matterRepository.findByIdAndOrgId(id, orgId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.lawauto.backend.matter;

import com.lawauto.backend.matter.dto.MatterListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/matters")
public class MatterController {

    private final MatterService matterService;

    public MatterController(MatterService matterService) {
        this.matterService = matterService;
    }

    /**
     * GET /api/matters?orgId={orgId}&page=0&size=20
     * Returns a paginated, optimized list of matters for the requesting organization.
     */
    @GetMapping
    public Page<MatterListDto> listMatters(@RequestParam UUID orgId, Pageable pageable) {
        return matterService.listMatters(orgId, pageable);
    }

    /**
     * GET /api/matters/{matterId}?orgId={orgId}
     * Returns a highly optimized, comprehensive Read Model for the Matter Detail view.
     */
    @org.springframework.web.bind.annotation.GetMapping("/{matterId}")
    public com.lawauto.backend.matter.dto.MatterDetailDto getMatterDetail(
            @org.springframework.web.bind.annotation.PathVariable UUID matterId,
            @RequestParam UUID orgId) {
        return matterService.getMatterDetail(orgId, matterId);
    }
}

package com.lawauto.backend.matter.dto;

import java.util.List;

/**
 * Aggregate read-model for the dashboard. Backed by a single COUNT(*) FILTER
 * query plus a small bounded LIMIT 5 query, instead of the previous
 * approach of fetching size=100 full MatterListDto rows and reducing them
 * in the browser.
 */
public record MatterDashboardStatsDto(
        long activeCount,
        long pendingCount,
        long closedCount,
        long totalCount,
        java.time.OffsetDateTime firstOpenedAt,
        List<MatterListDto> recentMatters
) {
}

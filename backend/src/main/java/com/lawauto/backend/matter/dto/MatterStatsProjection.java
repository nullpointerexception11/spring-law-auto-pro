package com.lawauto.backend.matter.dto;

/**
 * Interface projection for the dashboard aggregate stats query.
 * One row, four numbers — replaces fetching up to 100 full matter rows
 * (each with 3 lateral joins) just to compute counts client-side.
 */
public interface MatterStatsProjection {
    long getActiveCount();
    long getPendingCount();
    long getClosedCount();
    long getTotalCount();
    java.time.OffsetDateTime getFirstOpenedAt();
}

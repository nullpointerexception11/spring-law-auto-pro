package com.lawauto.backend.operations;

import java.util.UUID;

public final class DeleteRequestActions {
    private DeleteRequestActions() {}

    public record ReviewDeleteRequestRequest(UUID orgId, UUID reviewerUserId, String reason) {}
    public record ExecuteDeleteRequestRequest(UUID orgId, UUID executedByUserId) {}
}

package com.lawauto.backend.common;

public record PageMeta(
        int page,
        int size,
        long total,
        String sort
) {}

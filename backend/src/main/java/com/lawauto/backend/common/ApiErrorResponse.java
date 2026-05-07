package com.lawauto.backend.common;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String path,
        Map<String, String> fieldErrors
) {
    public static ApiErrorResponse of(int status, String error, String path) {
        return new ApiErrorResponse(Instant.now(), status, error, path, null);
    }

    public static ApiErrorResponse of(int status, String error, String path, Map<String, String> fieldErrors) {
        return new ApiErrorResponse(Instant.now(), status, error, path, fieldErrors);
    }
}

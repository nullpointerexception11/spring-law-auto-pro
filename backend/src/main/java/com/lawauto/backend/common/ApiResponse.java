package com.lawauto.backend.common;

public record ApiResponse<T>(
        String status,
        T data,
        Object meta
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("ok", data, null);
    }

    public static <T> ApiResponse<T> ok(T data, Object meta) {
        return new ApiResponse<>("ok", data, meta);
    }
}

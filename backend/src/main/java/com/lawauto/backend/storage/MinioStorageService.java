package com.lawauto.backend.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

/**
 * MinIO implementation of StorageService.
 * Placeholder for actual MinIO client integration.
 */
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    @Override
    public void put(UUID storageKey, InputStream inputStream, String contentType) {
        // TODO: Implement MinIO putObject
    }

    @Override
    public Resource get(UUID storageKey) {
        // TODO: Implement MinIO getObject
        return null;
    }

    @Override
    public void delete(UUID storageKey) {
        // TODO: Implement MinIO removeObject
    }

    @Override
    public URL generateSignedUrl(UUID storageKey, String httpMethod, int expirySeconds) {
        // Interface ile uyumlu hale getirildi ✅
        // TODO: Implement MinIO presigned URL
        return null;
    }
}

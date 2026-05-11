package com.lawauto.backend.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Simple filesystem implementation for local development or on‑prem deployments.
 * Base directory is configured via `app.storage.local.path`.
 */
@Service
@RequiredArgsConstructor
public class LocalStorageService implements StorageService {

    private final Path baseDir; // injected via configuration

    private Path resolve(UUID key) {
        return baseDir.resolve(key.toString());
    }

    @Override
    public void put(UUID key, InputStream data, String contentType) {
        try {
            Files.createDirectories(baseDir);
            Files.copy(data, resolve(key), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource get(UUID key) {
        Path p = resolve(key);
        return new FileSystemResource(p);
    }

    @Override
    public void delete(UUID key) {
        try {
            Files.deleteIfExists(resolve(key));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public URL generateSignedUrl(UUID key, String httpMethod, int expirySeconds) {
        // For local storage we just return a plain file URL – no real signing.
        try {
            return resolve(key).toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}

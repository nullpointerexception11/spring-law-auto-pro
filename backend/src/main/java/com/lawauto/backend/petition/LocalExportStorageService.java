package com.lawauto.backend.petition;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalExportStorageService {
    private final Path root;

    public LocalExportStorageService(@Value("${app.exports.root:C:/tmp/law-auto-exports}") String rootPath) {
        this.root = Path.of(rootPath);
    }

    public void save(String storageKey, byte[] bytes) {
        try {
            Path target = root.resolve(storageKey).normalize();
            Files.createDirectories(target.getParent());
            Files.write(target, bytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public byte[] read(String storageKey) {
        try {
            Path target = root.resolve(storageKey).normalize();
            return Files.readAllBytes(target);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

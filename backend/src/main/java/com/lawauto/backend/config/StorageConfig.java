package com.lawauto.backend.config;

import com.lawauto.backend.storage.StorageService;
import com.lawauto.backend.storage.S3StorageService;
import com.lawauto.backend.storage.MinioStorageService;
import com.lawauto.backend.storage.LocalStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

import java.nio.file.Path;

/**
 * Chooses the concrete {@link StorageService} bean based on the property
 * {@code app.storage.provider} (values: LOCAL, S3, MINIO).
 */
@Configuration
@RequiredArgsConstructor
public class StorageConfig {

    @Value("${app.storage.provider:LOCAL}")
    private String provider;

    /** Local filesystem implementation */
    @Bean
    @ConditionalOnProperty(name = "app.storage.provider", havingValue = "LOCAL")
    public StorageService localStorage(@Value("${app.storage.local.path}") String path) {
        return new LocalStorageService(Path.of(path));
    }

    /** AWS S3 implementation */
    @Bean
    @ConditionalOnProperty(name = "app.storage.provider", havingValue = "S3")
    public StorageService s3Storage(S3Client s3Client,
                                   @Value("${app.storage.s3.bucket}") String bucket) {
        return new S3StorageService(s3Client, bucket);
    }

    /** MinIO implementation – placeholder, add real client if needed */
    @Bean
    @ConditionalOnProperty(name = "app.storage.provider", havingValue = "MINIO")
    public StorageService minioStorage() {
        // TODO: inject MinIO client and bucket configuration
        return new MinioStorageService();
    }
}

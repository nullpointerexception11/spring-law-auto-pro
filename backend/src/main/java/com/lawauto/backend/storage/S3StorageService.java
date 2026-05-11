package com.lawauto.backend.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

/**
 * AWS S3 implementation of {@link StorageService}.
 *
 * The bean expects two constructor arguments provided by Spring:
 *   - an {@link S3Client} configured via the AWS SDK properties,
 *   - the target bucket name (injected via @Value).
 */
@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final String bucketName; // e.g. "lawauto-files"

    @Override
    public void put(UUID key, InputStream data, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key.toString())
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromInputStream(data, -1));
    }

    @Override
    public Resource get(UUID key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key.toString())
                .build();
        ResponseInputStream<GetObjectResponse> is = s3Client.getObject(request);
        return new InputStreamResource(is);
    }

    @Override
    public void delete(UUID key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key.toString())
                .build();
        s3Client.deleteObject(request);
    }

    @Override
    public URL generateSignedUrl(UUID key, String httpMethod, int expirySeconds) {
        // Production code would use S3Presigner to create a presigned URL.
        // For this demo we throw an exception to indicate that it is not yet implemented.
        throw new UnsupportedOperationException("Signed URL generation not implemented for S3 in this demo");
    }
}

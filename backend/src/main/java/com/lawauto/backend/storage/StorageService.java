package com.lawauto.backend.storage;

import org.springframework.core.io.Resource;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

/**
 * High‑level abstraction for all binary storage back‑ends.
 * Implementations must be stateless Spring beans.
 */
public interface StorageService {

    /** Store bytes under the given key (key is usually a UUID). */
    void put(UUID key, InputStream data, String contentType);

    /** Retrieve the object as a Spring {@link Resource}. */
    Resource get(UUID key);

    /** Delete the object */
    void delete(UUID key);

    /**
     * Return a time‑limited signed URL (e.g. for S3 presigned GET/PUT).
     * Implementations decide the actual expiry.
     */
    URL generateSignedUrl(UUID key, String httpMethod, int expirySeconds);
}

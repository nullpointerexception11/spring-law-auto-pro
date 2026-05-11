package com.lawauto.backend.document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {

    /**
     * Tenant‑scoped SHA‑256 araması; aynı dosyanın aynı org içinde birden fazla kez yüklenmesini önler.
     */
    Optional<FileMetadata> findByOrgIdAndSha256(UUID orgId, String sha256);
}

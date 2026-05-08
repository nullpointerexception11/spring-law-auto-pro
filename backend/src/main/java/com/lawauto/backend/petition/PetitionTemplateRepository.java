package com.lawauto.backend.petition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetitionTemplateRepository extends JpaRepository<PetitionTemplate, UUID> {
    List<PetitionTemplate> findByOrgIdOrderByNameAscVersionDesc(UUID orgId);
    Optional<PetitionTemplate> findByIdAndOrgId(UUID id, UUID orgId);
}

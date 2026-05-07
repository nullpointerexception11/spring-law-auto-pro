package com.lawauto.backend.petition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetitionTemplateRepository extends JpaRepository<PetitionTemplateEntity, UUID> {
    List<PetitionTemplateEntity> findByOrgIdOrderByNameAscVersionDesc(UUID orgId);
    Optional<PetitionTemplateEntity> findByIdAndOrgId(UUID id, UUID orgId);
}

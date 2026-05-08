package com.lawauto.backend.petition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PetitionRepository extends JpaRepository<Petition, UUID> {
    List<Petition> findAllByMatterIdAndDeletedAtIsNull(UUID matterId);
}

package com.lawauto.backend.cases;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UniversalEventRepository extends JpaRepository<UniversalEvent, UUID> {
}

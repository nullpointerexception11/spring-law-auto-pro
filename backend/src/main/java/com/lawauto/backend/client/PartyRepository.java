package com.lawauto.backend.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PartyRepository extends JpaRepository<Party, UUID> {
    // Basic Party operations scoped to Org will be added
}

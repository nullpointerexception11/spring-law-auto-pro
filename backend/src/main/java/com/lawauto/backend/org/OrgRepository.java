package com.lawauto.backend.org;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrgRepository extends JpaRepository<Org, UUID> {
    Optional<Org> findByName(String name);
}

package com.lawauto.backend.org;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrgRepository extends JpaRepository<Org, UUID> {
}

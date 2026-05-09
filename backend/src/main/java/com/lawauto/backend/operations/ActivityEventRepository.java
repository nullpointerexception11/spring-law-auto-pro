package com.lawauto.backend.operations;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ActivityEventRepository extends JpaRepository<ActivityEvent, UUID> {
}

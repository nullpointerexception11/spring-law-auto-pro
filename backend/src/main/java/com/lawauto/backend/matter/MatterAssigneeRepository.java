package com.lawauto.backend.matter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MatterAssigneeRepository extends JpaRepository<MatterAssignee, UUID> {
    
    @Query("SELECT COUNT(ma) > 0 FROM MatterAssignee ma WHERE ma.matter.id = :matterId AND ma.user.id = :userId")
    boolean hasAccess(UUID matterId, UUID userId);
}

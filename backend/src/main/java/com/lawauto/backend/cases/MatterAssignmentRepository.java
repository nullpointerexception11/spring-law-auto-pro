package com.lawauto.backend.cases;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatterAssignmentRepository extends JpaRepository<MatterAssignment, MatterAssignmentId> {
}

package com.lawauto.backend.user;

import com.lawauto.backend.user.UserRoleEntity.UserRoleId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleId> {
    List<UserRoleEntity> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}

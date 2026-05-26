package com.lawauto.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailCanonical(String emailCanonical);
    
    @Query("SELECT u FROM User u JOIN u.org o WHERE o.slug = :orgSlug AND u.emailCanonical = :email")
    Optional<User> findByOrgSlugAndEmailCanonical(@Param("orgSlug") String orgSlug, @Param("email") String email);
}

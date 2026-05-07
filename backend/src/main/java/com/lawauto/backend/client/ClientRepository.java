package com.lawauto.backend.client;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Page<Client> findByOrgIdAndDeletedAtIsNull(UUID orgId, Pageable pageable);
    long countByOrgIdAndDeletedAtIsNull(UUID orgId);

    @Query(value = """
            select c.* from \"Client\" c
            where c.\"orgId\" = :orgId
              and c.\"deletedAt\" is null
              and (
                c.\"createdByUserId\" = :userId
                or exists (
                  select 1 from \"ClientLawyer\" cl
                  where cl.\"clientId\" = c.\"id\"
                    and cl.\"lawyerUserId\" = :userId
                    and cl.\"endedAt\" is null
                )
              )
            """,
            countQuery = """
            select count(1) from \"Client\" c
            where c.\"orgId\" = :orgId
              and c.\"deletedAt\" is null
              and (
                c.\"createdByUserId\" = :userId
                or exists (
                  select 1 from \"ClientLawyer\" cl
                  where cl.\"clientId\" = c.\"id\"
                    and cl.\"lawyerUserId\" = :userId
                    and cl.\"endedAt\" is null
                )
              )
            """,
            nativeQuery = true)
    Page<Client> findVisibleForLawyer(@Param("orgId") UUID orgId, @Param("userId") UUID userId, Pageable pageable);

    @Query(value = """
            select c.* from \"Client\" c
            where c.\"orgId\" = :orgId
              and c.\"deletedAt\" is null
              and exists (
                select 1
                from \"ClientLawyer\" cl
                join \"SecretaryLawyer\" sl on sl.\"lawyerUserId\" = cl.\"lawyerUserId\"
                where cl.\"clientId\" = c.\"id\"
                  and cl.\"endedAt\" is null
                  and sl.\"secretaryUserId\" = :userId
                  and sl.\"orgId\" = :orgId
                  and sl.\"endedAt\" is null
              )
            """,
            countQuery = """
            select count(1) from \"Client\" c
            where c.\"orgId\" = :orgId
              and c.\"deletedAt\" is null
              and exists (
                select 1
                from \"ClientLawyer\" cl
                join \"SecretaryLawyer\" sl on sl.\"lawyerUserId\" = cl.\"lawyerUserId\"
                where cl.\"clientId\" = c.\"id\"
                  and cl.\"endedAt\" is null
                  and sl.\"secretaryUserId\" = :userId
                  and sl.\"orgId\" = :orgId
                  and sl.\"endedAt\" is null
              )
            """,
            nativeQuery = true)
    Page<Client> findVisibleForSecretary(@Param("orgId") UUID orgId, @Param("userId") UUID userId, Pageable pageable);

    @Query(value = """
            select count(1) from \"Client\" c
            where c.\"orgId\" = :orgId
              and c.\"deletedAt\" is null
              and (
                c.\"createdByUserId\" = :userId
                or exists (
                  select 1 from \"ClientLawyer\" cl
                  where cl.\"clientId\" = c.\"id\"
                    and cl.\"lawyerUserId\" = :userId
                    and cl.\"endedAt\" is null
                )
              )
            """, nativeQuery = true)
    long countVisibleForLawyer(@Param("orgId") UUID orgId, @Param("userId") UUID userId);

    @Query(value = """
            select count(1) from \"Client\" c
            where c.\"orgId\" = :orgId
              and c.\"deletedAt\" is null
              and exists (
                select 1
                from \"ClientLawyer\" cl
                join \"SecretaryLawyer\" sl on sl.\"lawyerUserId\" = cl.\"lawyerUserId\"
                where cl.\"clientId\" = c.\"id\"
                  and cl.\"endedAt\" is null
                  and sl.\"secretaryUserId\" = :userId
                  and sl.\"orgId\" = :orgId
                  and sl.\"endedAt\" is null
              )
            """, nativeQuery = true)
    long countVisibleForSecretary(@Param("orgId") UUID orgId, @Param("userId") UUID userId);
}

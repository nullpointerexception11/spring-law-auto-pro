package com.lawauto.backend.cases;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CaseRepository extends JpaRepository<CaseEntity, UUID> {
    Page<CaseEntity> findByOrgIdAndDeletedAtIsNull(UUID orgId, Pageable pageable);
    long countByOrgIdAndDeletedAtIsNull(UUID orgId);

    @Query(value = """
            select c.* from Case c
            where c.orgId = :orgId
              and c.deletedAt is null
              and (
                c.createdByUserId = :userId
                or exists (
                  select 1 from CaseLawyer cl
                  where cl.caseId = c.id
                    and cl.lawyerUserId = :userId
                    and cl.endedAt is null
                )
              )
            """,
            countQuery = """
            select count(1) from Case c
            where c.orgId = :orgId
              and c.deletedAt is null
              and (
                c.createdByUserId = :userId
                or exists (
                  select 1 from CaseLawyer cl
                  where cl.caseId = c.id
                    and cl.lawyerUserId = :userId
                    and cl.endedAt is null
                )
              )
            """,
            nativeQuery = true)
    Page<CaseEntity> findVisibleForLawyer(@Param("orgId") UUID orgId, @Param("userId") UUID userId, Pageable pageable);

    @Query(value = """
            select c.* from Case c
            where c.orgId = :orgId
              and c.deletedAt is null
              and exists (
                select 1
                from CaseLawyer cl
                join SecretaryLawyer sl on sl.lawyerUserId = cl.lawyerUserId
                where cl.caseId = c.id
                  and cl.endedAt is null
                  and sl.secretaryUserId = :userId
                  and sl.orgId = :orgId
                  and sl.endedAt is null
              )
            """,
            countQuery = """
            select count(1) from Case c
            where c.orgId = :orgId
              and c.deletedAt is null
              and exists (
                select 1
                from CaseLawyer cl
                join SecretaryLawyer sl on sl.lawyerUserId = cl.lawyerUserId
                where cl.caseId = c.id
                  and cl.endedAt is null
                  and sl.secretaryUserId = :userId
                  and sl.orgId = :orgId
                  and sl.endedAt is null
              )
            """,
            nativeQuery = true)
    Page<CaseEntity> findVisibleForSecretary(@Param("orgId") UUID orgId, @Param("userId") UUID userId, Pageable pageable);

    @Query(value = """
            select count(1) from Case c
            where c.orgId = :orgId
              and c.deletedAt is null
              and (
                c.createdByUserId = :userId
                or exists (
                  select 1 from CaseLawyer cl
                  where cl.caseId = c.id
                    and cl.lawyerUserId = :userId
                    and cl.endedAt is null
                )
              )
            """, nativeQuery = true)
    long countVisibleForLawyer(@Param("orgId") UUID orgId, @Param("userId") UUID userId);

    @Query(value = """
            select count(1) from Case c
            where c.orgId = :orgId
              and c.deletedAt is null
              and exists (
                select 1
                from CaseLawyer cl
                join SecretaryLawyer sl on sl.lawyerUserId = cl.lawyerUserId
                where cl.caseId = c.id
                  and cl.endedAt is null
                  and sl.secretaryUserId = :userId
                  and sl.orgId = :orgId
                  and sl.endedAt is null
              )
            """, nativeQuery = true)
    long countVisibleForSecretary(@Param("orgId") UUID orgId, @Param("userId") UUID userId);
}

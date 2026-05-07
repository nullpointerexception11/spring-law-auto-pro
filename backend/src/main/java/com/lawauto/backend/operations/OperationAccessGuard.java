package com.lawauto.backend.operations;

import com.lawauto.backend.auth.AuthPrincipal;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OperationAccessGuard {
    private final NamedParameterJdbcTemplate jdbc;

    public OperationAccessGuard(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void requireCaseAccess(AuthPrincipal principal, UUID caseId) {
        if ("ADMIN".equals(principal.role())) return;

        String sql = switch (principal.role()) {
            case "LAWYER" -> """
                    select count(1)
                    from Case c
                    where c.id = :caseId
                      and c.orgId = :orgId
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
                    """;
            case "SECRETARY" -> """
                    select count(1)
                    from Case c
                    where c.id = :caseId
                      and c.orgId = :orgId
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
                    """;
            default -> throw new IllegalArgumentException("Forbidden: unsupported role");
        };

        Integer count = jdbc.queryForObject(sql, params(principal, caseId), Integer.class);
        if (count == null || count == 0) {
            throw new IllegalArgumentException("Forbidden: no access to case");
        }
    }

    public void requireClientAccess(AuthPrincipal principal, UUID clientId) {
        if ("ADMIN".equals(principal.role())) return;

        String sql = switch (principal.role()) {
            case "LAWYER" -> """
                    select count(1)
                    from Client c
                    where c.id = :clientId
                      and c.orgId = :orgId
                      and c.deletedAt is null
                      and (
                        c.createdByUserId = :userId
                        or exists (
                          select 1 from ClientLawyer cl
                          where cl.clientId = c.id
                            and cl.lawyerUserId = :userId
                            and cl.endedAt is null
                        )
                      )
                    """;
            case "SECRETARY" -> """
                    select count(1)
                    from Client c
                    where c.id = :clientId
                      and c.orgId = :orgId
                      and c.deletedAt is null
                      and exists (
                        select 1
                        from ClientLawyer cl
                        join SecretaryLawyer sl on sl.lawyerUserId = cl.lawyerUserId
                        where cl.clientId = c.id
                          and cl.endedAt is null
                          and sl.secretaryUserId = :userId
                          and sl.orgId = :orgId
                          and sl.endedAt is null
                      )
                    """;
            default -> throw new IllegalArgumentException("Forbidden: unsupported role");
        };

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("clientId", clientId)
                .addValue("orgId", principal.orgId())
                .addValue("userId", principal.userId());

        Integer count = jdbc.queryForObject(sql, source, Integer.class);
        if (count == null || count == 0) {
            throw new IllegalArgumentException("Forbidden: no access to client");
        }
    }

    private MapSqlParameterSource params(AuthPrincipal principal, UUID caseId) {
        return new MapSqlParameterSource()
                .addValue("caseId", caseId)
                .addValue("orgId", principal.orgId())
                .addValue("userId", principal.userId());
    }
}

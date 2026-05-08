package com.lawauto.backend.operations;

import com.lawauto.backend.auth.AuthPrincipal;
import java.util.Objects;
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

    /**
     * PRESTIGE LMMS: Check if user has access to a Matter.
     */
    public void requireMatterAccess(AuthPrincipal principal, UUID matterId) {
        if ("ADMIN".equals(principal.role()) || "ORG_ADMIN".equals(principal.role())) return;

        String sql = switch (principal.role()) {
            case "LAWYER" -> """
                    select count(1)
                    from "Matter" m
                    where m.id = :matterId
                      and m.orgId = :orgId
                      and m.recordStatus = 'ACTIVE'
                      and (
                        m.createdByUserId = :userId
                        or exists (
                          select 1 from "MatterAssignment" ma
                          where ma.matterId = m.id
                            and ma.userId = :userId
                        )
                      )
                    """;
            case "SECRETARY" -> """
                    select count(1)
                    from "Matter" m
                    where m.id = :matterId
                      and m.orgId = :orgId
                      and m.recordStatus = 'ACTIVE'
                      and exists (
                        select 1
                        from "MatterAssignment" ma
                        join "User" u on u.id = ma.userId
                        where ma.matterId = m.id
                          and ma.userId = :userId -- Placeholder for secretary logic if needed
                      )
                    """;
            default -> throw new IllegalArgumentException("Forbidden: unsupported role");
        };

        Integer count = jdbc.queryForObject(sql, Objects.requireNonNull(params(principal, matterId)), Integer.class);
        if (count == null || count == 0) {
            throw new IllegalArgumentException("Forbidden: no access to matter");
        }
    }

    /**
     * PRESTIGE LMMS: Check if user has access to a Party.
     */
    public void requirePartyAccess(AuthPrincipal principal, UUID partyId) {
        if ("ADMIN".equals(principal.role()) || "ORG_ADMIN".equals(principal.role())) return;

        String sql = """
                    select count(1)
                    from "Party" p
                    where p.id = :partyId
                      and p.orgId = :orgId
                      and p.status = 'ACTIVE'
                    """;

        MapSqlParameterSource source = new MapSqlParameterSource()
                .addValue("partyId", partyId)
                .addValue("orgId", principal.orgId())
                .addValue("userId", principal.userId());

        Integer count = jdbc.queryForObject(sql, Objects.requireNonNull(source), Integer.class);
        if (count == null || count == 0) {
            throw new IllegalArgumentException("Forbidden: no access to party");
        }
    }

    private MapSqlParameterSource params(AuthPrincipal principal, UUID matterId) {
        return new MapSqlParameterSource()
                .addValue("matterId", matterId)
                .addValue("orgId", principal.orgId())
                .addValue("userId", principal.userId());
    }
}

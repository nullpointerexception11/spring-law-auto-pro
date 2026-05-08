package com.lawauto.backend.operations;

import static com.lawauto.backend.operations.OperationDtos.*;

import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CaseFeeTermsService {
    private final NamedParameterJdbcTemplate jdbc;
    public CaseFeeTermsService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Optional<CaseFeeTermsDto> findByCaseId(UUID matterId) {
        String sql = """
                select "id", "orgId", "matterId", "model", "baseFeeAmount", "successFeePercent", "currency", "notes", "createdByUserId", "createdAt"
                from "MatterFeeTerms" where "matterId" = :matterId
                """;
        return jdbc.query(sql, new MapSqlParameterSource("matterId", matterId), (rs, n) -> new CaseFeeTermsDto(
                rs.getObject("id", UUID.class), 
                rs.getObject("orgId", UUID.class), 
                rs.getObject("matterId", UUID.class),
                rs.getString("model"), 
                rs.getBigDecimal("baseFeeAmount"), 
                rs.getBigDecimal("successFeePercent"),
                rs.getString("currency"), 
                rs.getString("notes"), 
                rs.getObject("createdByUserId", UUID.class),
                rs.getObject("createdAt", java.time.OffsetDateTime.class)
        )).stream().findFirst();
    }

    @Transactional
    public void upsert(UpsertCaseFeeTermsRequest req) {
        String sql = """
                insert into "MatterFeeTerms" ("id", "orgId", "matterId", "model", "baseFeeAmount", "successFeePercent", "currency", "notes", "createdByUserId", "updatedAt")
                values (:id, :orgId, :matterId, :model, :baseFeeAmount, :successFeePercent, :currency, :notes, :createdByUserId, now())
                on conflict ("matterId") do update set
                  "model" = excluded."model",
                  "baseFeeAmount" = excluded."baseFeeAmount",
                  "successFeePercent" = excluded."successFeePercent",
                  "currency" = excluded."currency",
                  "notes" = excluded."notes",
                  "updatedAt" = now()
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", UUID.randomUUID())
                .addValue("orgId", req.orgId())
                .addValue("matterId", req.matterId())
                .addValue("model", req.model())
                .addValue("baseFeeAmount", req.baseFeeAmount())
                .addValue("successFeePercent", req.successFeePercent())
                .addValue("currency", req.currency() == null ? "TRY" : req.currency())
                .addValue("notes", req.notes())
                .addValue("createdByUserId", req.createdByUserId()));
    }
}

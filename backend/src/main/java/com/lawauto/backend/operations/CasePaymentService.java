package com.lawauto.backend.operations;

import static com.lawauto.backend.operations.OperationDtos.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CasePaymentService {
    private final NamedParameterJdbcTemplate jdbc;
    public CasePaymentService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<CasePaymentDto> listByOrg(UUID orgId) {
        String sql = """
                select "id", "orgId", "matterId", "amount", "currency", "paidAt", "method", "note", "receiptFileId", "recordedByUserId", "createdAt"
                from "Payment" 
                where "orgId" = :orgId and "deletedAt" is null 
                order by "paidAt" desc
                """;
        return jdbc.query(sql, new MapSqlParameterSource("orgId", orgId), (rs, n) -> new CasePaymentDto(
                rs.getObject("id", UUID.class), 
                rs.getObject("orgId", UUID.class), 
                rs.getObject("matterId", UUID.class),
                rs.getBigDecimal("amount"), 
                rs.getString("currency"), 
                rs.getObject("paidAt", java.time.OffsetDateTime.class),
                rs.getString("method"), 
                rs.getString("note"), 
                rs.getObject("receiptFileId", UUID.class),
                rs.getObject("recordedByUserId", UUID.class), 
                rs.getObject("createdAt", java.time.OffsetDateTime.class)
        ));
    }

    @Transactional
    public UUID create(CreateCasePaymentRequest req) {
        UUID id = UUID.randomUUID();
        String sql = """
                insert into "Payment" ("id", "orgId", "matterId", "amount", "currency", "paidAt", "method", "note", "receiptFileId", "recordedByUserId", "updatedAt")
                values (:id, :orgId, :matterId, :amount, :currency, :paidAt, :method, :note, :receiptFileId, :recordedByUserId, now())
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("orgId", req.orgId())
                .addValue("matterId", req.matterId())
                .addValue("amount", req.amount() == null ? BigDecimal.ZERO : req.amount())
                .addValue("currency", req.currency() == null ? "TRY" : req.currency())
                .addValue("paidAt", req.paidAt() == null ? java.time.OffsetDateTime.now() : req.paidAt())
                .addValue("method", req.method() == null ? "BANK_TRANSFER" : req.method())
                .addValue("note", req.note())
                .addValue("receiptFileId", req.receiptFileId())
                .addValue("recordedByUserId", req.recordedByUserId()));
        return id;
    }
}

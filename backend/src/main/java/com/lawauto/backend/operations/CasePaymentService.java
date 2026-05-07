package com.lawauto.backend.operations;

import static com.lawauto.backend.operations.OperationDtos.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
                select \"id\", \"orgId\", \"caseId\", \"amount\", \"currency\", \"paidAt\", \"method\", \"note\", \"receiptFileId\", \"recordedByUserId\", \"createdAt\"
                from \"CasePayment\" where \"orgId\"=:orgId and \"deletedAt\" is null order by \"paidAt\" desc
                """;
        return jdbc.query(sql, new MapSqlParameterSource("orgId", orgId), (rs, n) -> new CasePaymentDto(
                rs.getObject("id", UUID.class), rs.getObject("orgId", UUID.class), rs.getObject("caseId", UUID.class),
                rs.getBigDecimal("amount"), rs.getString("currency"), rs.getTimestamp("paidAt").toLocalDateTime(),
                rs.getString("method"), rs.getString("note"), rs.getObject("receiptFileId", UUID.class),
                rs.getObject("recordedByUserId", UUID.class), rs.getTimestamp("createdAt").toLocalDateTime()
        ));
    }

    @Transactional
    public UUID create(CreateCasePaymentRequest req) {
        UUID id = UUID.randomUUID();
        String sql = """
                insert into \"CasePayment\" (\"id\", \"orgId\", \"caseId\", \"amount\", \"currency\", \"paidAt\", \"method\", \"note\", \"receiptFileId\", \"recordedByUserId\", \"updatedAt\")
                values (:id, :orgId, :caseId, :amount, :currency, :paidAt, :method::\"PaymentMethod\", :note, :receiptFileId, :recordedByUserId, now())
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id).addValue("orgId", req.orgId()).addValue("caseId", req.caseId())
                .addValue("amount", req.amount() == null ? BigDecimal.ZERO : req.amount())
                .addValue("currency", req.currency() == null ? "TRY" : req.currency())
                .addValue("paidAt", req.paidAt() == null ? Timestamp.valueOf(java.time.LocalDateTime.now()) : Timestamp.valueOf(req.paidAt()))
                .addValue("method", req.method() == null ? "BANK_TRANSFER" : req.method())
                .addValue("note", req.note()).addValue("receiptFileId", req.receiptFileId())
                .addValue("recordedByUserId", req.recordedByUserId()));
        return id;
    }
}

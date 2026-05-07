package com.lawauto.backend.operations;

import static com.lawauto.backend.operations.OperationDtos.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeadlineService {
    private final NamedParameterJdbcTemplate jdbc;

    public DeadlineService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<DeadlineDto> listByOrg(UUID orgId) {
        String sql = """
                select \"id\", \"orgId\", \"caseId\", \"type\", \"dueAt\", \"remindAt\", \"status\", \"notes\", \"createdByUserId\", \"createdAt\"
                from \"Deadline\"
                where \"orgId\" = :orgId and \"deletedAt\" is null
                order by \"dueAt\" asc
                """;
        return jdbc.query(sql, new MapSqlParameterSource("orgId", orgId), (rs, n) -> new DeadlineDto(
                rs.getObject("id", UUID.class),
                rs.getObject("orgId", UUID.class),
                rs.getObject("caseId", UUID.class),
                rs.getString("type"),
                rs.getTimestamp("dueAt").toLocalDateTime(),
                rs.getTimestamp("remindAt") == null ? null : rs.getTimestamp("remindAt").toLocalDateTime(),
                rs.getString("status"),
                rs.getString("notes"),
                rs.getObject("createdByUserId", UUID.class),
                rs.getTimestamp("createdAt").toLocalDateTime()
        ));
    }

    @Transactional
    public UUID create(CreateDeadlineRequest req) {
        UUID id = UUID.randomUUID();
        String sql = """
                insert into \"Deadline\" (\"id\", \"orgId\", \"caseId\", \"type\", \"dueAt\", \"remindAt\", \"status\", \"notes\", \"createdByUserId\", \"updatedAt\")
                values (:id, :orgId, :caseId, :type, :dueAt, :remindAt, :status, :notes, :createdByUserId, now())
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("orgId", req.orgId())
                .addValue("caseId", req.caseId())
                .addValue("type", req.type())
                .addValue("dueAt", Timestamp.valueOf(req.dueAt()))
                .addValue("remindAt", req.remindAt() == null ? null : Timestamp.valueOf(req.remindAt()))
                .addValue("status", req.status() == null ? "OPEN" : req.status())
                .addValue("notes", req.notes())
                .addValue("createdByUserId", req.createdByUserId()));
        return id;
    }
}

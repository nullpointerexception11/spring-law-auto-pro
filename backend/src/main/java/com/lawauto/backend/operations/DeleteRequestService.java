package com.lawauto.backend.operations;

import static com.lawauto.backend.operations.OperationDtos.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteRequestService {
    private final NamedParameterJdbcTemplate jdbc;
    public DeleteRequestService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<DeleteRequestDto> listByOrg(UUID orgId) {
        String sql = """
                select "id", "orgId", "entityType", "entityId", "mode", "status", "reason", "requestedByUserId", "requestedAt"
                from "DeleteRequest" where "orgId"=:orgId order by "requestedAt" desc
                """;
        return jdbc.query(sql, new MapSqlParameterSource("orgId", orgId), (rs, n) -> new DeleteRequestDto(
                rs.getObject("id", UUID.class), rs.getObject("orgId", UUID.class), rs.getString("entityType"),
                rs.getObject("entityId", UUID.class), rs.getString("mode"), rs.getString("status"), rs.getString("reason"),
                rs.getObject("requestedByUserId", UUID.class), rs.getTimestamp("requestedAt").toLocalDateTime()
        ));
    }

    @Transactional
    public UUID create(CreateDeleteRequestRequest req) {
        UUID id = UUID.randomUUID();
        String sql = """
                insert into "DeleteRequest" ("id", "orgId", "entityType", "entityId", "mode", "reason", "requestedByUserId")
                values (:id, :orgId, :entityType::"DeleteEntityType", :entityId, :mode::"DeleteMode", :reason, :requestedByUserId)
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id).addValue("orgId", req.orgId()).addValue("entityType", req.entityType())
                .addValue("entityId", req.entityId()).addValue("mode", req.mode() == null ? "SOFT" : req.mode())
                .addValue("reason", req.reason()).addValue("requestedByUserId", req.requestedByUserId()));
        return id;
    }

    @Transactional
    public void approve(UUID id, DeleteRequestActions.ReviewDeleteRequestRequest req) {
        String sql = """
                update "DeleteRequest"
                set "status"='APPROVED', "reviewedByUserId"=:reviewerUserId, "reviewedAt"=now(), "reason"=coalesce(:reason, "reason")
                where "id"=:id and "orgId"=:orgId and "status"='PENDING'
                """;
        int updated = jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("orgId", req.orgId())
                .addValue("reviewerUserId", req.reviewerUserId())
                .addValue("reason", req.reason()));
        if (updated == 0) throw new IllegalArgumentException("Delete request not found or not pending");
    }

    @Transactional
    public void reject(UUID id, DeleteRequestActions.ReviewDeleteRequestRequest req) {
        String sql = """
                update "DeleteRequest"
                set "status"='REJECTED', "reviewedByUserId"=:reviewerUserId, "reviewedAt"=now(), "reason"=coalesce(:reason, "reason")
                where "id"=:id and "orgId"=:orgId and "status"='PENDING'
                """;
        int updated = jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("orgId", req.orgId())
                .addValue("reviewerUserId", req.reviewerUserId())
                .addValue("reason", req.reason()));
        if (updated == 0) throw new IllegalArgumentException("Delete request not found or not pending");
    }

    @Transactional
    public void execute(UUID id, DeleteRequestActions.ExecuteDeleteRequestRequest req) {
        String sql = """
                select "entityType", "entityId", "mode" from "DeleteRequest"
                where "id"=:id and "orgId"=:orgId and "status"='APPROVED'
                """;
        List<Map<String, Object>> rows = jdbc.queryForList(sql, new MapSqlParameterSource()
                .addValue("id", id).addValue("orgId", req.orgId()));
        if (rows.isEmpty()) throw new IllegalArgumentException("Delete request not found or not approved");

        String entityType = (String) rows.get(0).get("entityType");
        UUID entityId = (UUID) rows.get(0).get("entityId");
        String mode = (String) rows.get(0).get("mode");

        if ("SOFT".equals(mode)) {
            applySoftDelete(entityType, entityId, req.executedByUserId());
        } else {
            applyHardDelete(entityType, entityId);
        }

        jdbc.update("""
                update "DeleteRequest"
                set "status"='EXECUTED', "executedByUserId"=:executedByUserId, "executedAt"=now()
                where "id"=:id and "orgId"=:orgId
                """, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("orgId", req.orgId())
                .addValue("executedByUserId", req.executedByUserId()));
    }

    private void applySoftDelete(String entityType, UUID entityId, UUID byUserId) {
        String table = switch (entityType) {
            case "CLIENT" -> "Client";
            case "CASE" -> "Case";
            case "PETITION" -> "Petition";
            case "EVIDENCE" -> "Evidence";
            case "HEARING" -> "Hearing";
            case "DEADLINE" -> "Deadline";
            case "CASE_PAYMENT" -> "CasePayment";
            case "CALENDAR_EVENT" -> "CalendarEvent";
            case "CLIENT_NOTE" -> "ClientNote";
            default -> throw new IllegalArgumentException("Soft delete not supported for entityType: " + entityType);
        };

        String sql = "update \"" + table + " set deletedAt=now(), deletedByUserId=:byUserId where id\"=:id";
        jdbc.update(sql, new MapSqlParameterSource().addValue("id", entityId).addValue("byUserId", byUserId));
    }

    private void applyHardDelete(String entityType, UUID entityId) {
        String table = switch (entityType) {
            case "CLIENT" -> "Client";
            case "CASE" -> "Case";
            case "PETITION" -> "Petition";
            case "EVIDENCE" -> "Evidence";
            case "HEARING" -> "Hearing";
            case "DEADLINE" -> "Deadline";
            case "CASE_PAYMENT" -> "CasePayment";
            case "CALENDAR_EVENT" -> "CalendarEvent";
            case "CLIENT_NOTE" -> "ClientNote";
            case "FILE_OBJECT" -> "FileObject";
            default -> throw new IllegalArgumentException("Unknown entityType: " + entityType);
        };

        String sql = "delete from \"" + table + " where id\"=:id";
        jdbc.update(sql, new MapSqlParameterSource().addValue("id", entityId));
    }
}

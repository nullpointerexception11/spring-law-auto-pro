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
public class CalendarEventService {
    private final NamedParameterJdbcTemplate jdbc;

    public CalendarEventService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<CalendarEventDto> listByOrg(UUID orgId) {
        String sql = """
                select \"id\", \"orgId\", \"ownerUserId\", \"startsAt\", \"endsAt\", \"title\", \"body\", \"remindAt\", \"relatedCaseId\", \"relatedClientId\", \"createdAt\"
                from \"CalendarEvent\"
                where \"orgId\" = :orgId and \"deletedAt\" is null
                order by \"startsAt\" asc
                """;
        return jdbc.query(sql, new MapSqlParameterSource("orgId", orgId), (rs, n) -> new CalendarEventDto(
                rs.getObject("id", UUID.class),
                rs.getObject("orgId", UUID.class),
                rs.getObject("ownerUserId", UUID.class),
                rs.getTimestamp("startsAt").toLocalDateTime(),
                rs.getTimestamp("endsAt") == null ? null : rs.getTimestamp("endsAt").toLocalDateTime(),
                rs.getString("title"),
                rs.getString("body"),
                rs.getTimestamp("remindAt") == null ? null : rs.getTimestamp("remindAt").toLocalDateTime(),
                rs.getObject("relatedCaseId", UUID.class),
                rs.getObject("relatedClientId", UUID.class),
                rs.getTimestamp("createdAt").toLocalDateTime()
        ));
    }

    @Transactional
    public UUID create(CreateCalendarEventRequest req) {
        UUID id = UUID.randomUUID();
        String sql = """
                insert into \"CalendarEvent\" (\"id\", \"orgId\", \"ownerUserId\", \"startsAt\", \"endsAt\", \"title\", \"body\", \"remindAt\", \"relatedCaseId\", \"relatedClientId\", \"updatedAt\")
                values (:id, :orgId, :ownerUserId, :startsAt, :endsAt, :title, :body, :remindAt, :relatedCaseId, :relatedClientId, now())
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("orgId", req.orgId())
                .addValue("ownerUserId", req.ownerUserId())
                .addValue("startsAt", Timestamp.valueOf(req.startsAt()))
                .addValue("endsAt", req.endsAt() == null ? null : Timestamp.valueOf(req.endsAt()))
                .addValue("title", req.title())
                .addValue("body", req.body())
                .addValue("remindAt", req.remindAt() == null ? null : Timestamp.valueOf(req.remindAt()))
                .addValue("relatedCaseId", req.relatedCaseId())
                .addValue("relatedClientId", req.relatedClientId()));
        return id;
    }
}

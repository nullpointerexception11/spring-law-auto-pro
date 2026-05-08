package com.lawauto.backend.operations;

import static com.lawauto.backend.operations.OperationDtos.*;

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
                select "id", "orgId", "ownerUserId", "startsAt", "endsAt", "title", "body", "remindAt", "relatedMatterId", "relatedPartyId", "createdAt"
                from "CalendarEvent"
                where "orgId" = :orgId and "deletedAt" is null
                order by "startsAt" asc
                """;
        return jdbc.query(sql, new MapSqlParameterSource("orgId", orgId), (rs, n) -> new CalendarEventDto(
                rs.getObject("id", UUID.class),
                rs.getObject("orgId", UUID.class),
                rs.getObject("ownerUserId", UUID.class),
                rs.getObject("startsAt", java.time.OffsetDateTime.class),
                rs.getObject("endsAt", java.time.OffsetDateTime.class),
                rs.getString("title"),
                rs.getString("body"),
                rs.getObject("remindAt", java.time.OffsetDateTime.class),
                rs.getObject("relatedMatterId", UUID.class),
                rs.getObject("relatedPartyId", UUID.class),
                rs.getObject("createdAt", java.time.OffsetDateTime.class)
        ));
    }

    @Transactional
    public UUID create(CreateCalendarEventRequest req) {
        UUID id = UUID.randomUUID();
        String sql = """
                insert into "CalendarEvent" ("id", "orgId", "ownerUserId", "startsAt", "endsAt", "title", "body", "remindAt", "relatedMatterId", "relatedPartyId", "updatedAt")
                values (:id, :orgId, :ownerUserId, :startsAt, :endsAt, :title, :body, :remindAt, :relatedMatterId, :relatedPartyId, now())
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("orgId", req.orgId())
                .addValue("ownerUserId", req.ownerUserId())
                .addValue("startsAt", req.startsAt())
                .addValue("endsAt", req.endsAt())
                .addValue("title", req.title())
                .addValue("body", req.body())
                .addValue("remindAt", req.remindAt())
                .addValue("relatedMatterId", req.relatedMatterId())
                .addValue("relatedPartyId", req.relatedPartyId()));
        return id;
    }
}

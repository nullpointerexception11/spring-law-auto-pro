package com.lawauto.backend.operations;

import static com.lawauto.backend.operations.OperationDtos.*;

import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HearingService {
    private final NamedParameterJdbcTemplate jdbc;

    public HearingService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<HearingDto> listByOrg(UUID orgId) {
        String sql = """
                select "id", "orgId", "matterId", "hearingAt", "court", "notes", "result", "createdByUserId", "createdAt"
                from "Hearing"
                where "orgId" = :orgId and "deletedAt" is null
                order by "hearingAt" asc
                """;
        return jdbc.query(sql, new MapSqlParameterSource("orgId", orgId), (rs, n) -> new HearingDto(
                rs.getObject("id", UUID.class),
                rs.getObject("orgId", UUID.class),
                rs.getObject("matterId", UUID.class),
                rs.getObject("hearingAt", java.time.OffsetDateTime.class),
                rs.getString("court"),
                rs.getString("notes"),
                rs.getString("result"),
                rs.getObject("createdByUserId", UUID.class),
                rs.getObject("createdAt", java.time.OffsetDateTime.class)
        ));
    }

    @Transactional
    public UUID create(CreateHearingRequest req) {
        UUID id = UUID.randomUUID();
        String sql = """
                insert into "Hearing" ("id", "orgId", "matterId", "hearingAt", "court", "notes", "result", "createdByUserId", "updatedAt")
                values (:id, :orgId, :matterId, :hearingAt, :court, :notes, :result, :createdByUserId, now())
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("orgId", req.orgId())
                .addValue("matterId", req.matterId())
                .addValue("hearingAt", req.hearingAt())
                .addValue("court", req.court())
                .addValue("notes", req.notes())
                .addValue("result", req.result())
                .addValue("createdByUserId", req.createdByUserId()));
        return id;
    }
}

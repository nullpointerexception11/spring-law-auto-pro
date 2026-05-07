package com.lawauto.backend.operations;

import static com.lawauto.backend.operations.OperationDtos.*;

import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientNoteService {
    private final NamedParameterJdbcTemplate jdbc;
    public ClientNoteService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<ClientNoteDto> listByOrg(UUID orgId) {
        String sql = """
                select id, orgId, clientId, body, visibility, createdByUserId, createdAt
                from ClientNote where orgId=:orgId and deletedAt is null order by createdAt desc
                """;
        return jdbc.query(sql, new MapSqlParameterSource("orgId", orgId), (rs, n) -> new ClientNoteDto(
                rs.getObject("id", UUID.class), rs.getObject("orgId", UUID.class), rs.getObject("clientId", UUID.class),
                rs.getString("body"), rs.getString("visibility"), rs.getObject("createdByUserId", UUID.class),
                rs.getTimestamp("createdAt").toLocalDateTime()
        ));
    }

    @Transactional
    public UUID create(CreateClientNoteRequest req) {
        UUID id = UUID.randomUUID();
        String sql = """
                insert into ClientNote (id, orgId, clientId, body, visibility, createdByUserId)
                values (:id, :orgId, :clientId, :body, :visibility, :createdByUserId)
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id).addValue("orgId", req.orgId()).addValue("clientId", req.clientId())
                .addValue("body", req.body()).addValue("visibility", req.visibility() == null ? "LAWYERS" : req.visibility())
                .addValue("createdByUserId", req.createdByUserId()));
        return id;
    }
}

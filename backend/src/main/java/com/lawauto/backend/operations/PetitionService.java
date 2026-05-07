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
public class PetitionService {
    private final NamedParameterJdbcTemplate jdbc;
    public PetitionService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<PetitionDto> listByOrg(UUID orgId) {
        String sql = """
                select id, orgId, caseId, title, body, fileId, createdByUserId, createdAt
                from Petition where orgId=:orgId and deletedAt is null order by createdAt desc
                """;
        return jdbc.query(sql, new MapSqlParameterSource("orgId", orgId), (rs, n) -> new PetitionDto(
                rs.getObject("id", UUID.class), rs.getObject("orgId", UUID.class), rs.getObject("caseId", UUID.class),
                rs.getString("title"), rs.getString("body"), rs.getObject("fileId", UUID.class),
                rs.getObject("createdByUserId", UUID.class), rs.getTimestamp("createdAt").toLocalDateTime()
        ));
    }

    @Transactional
    public UUID create(CreatePetitionRequest req) {
        UUID id = UUID.randomUUID();
        String sql = """
                insert into Petition (id, orgId, caseId, title, body, fileId, createdByUserId, updatedAt)
                values (:id, :orgId, :caseId, :title, :body, :fileId, :createdByUserId, now())
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id).addValue("orgId", req.orgId()).addValue("caseId", req.caseId())
                .addValue("title", req.title()).addValue("body", req.body()).addValue("fileId", req.fileId())
                .addValue("createdByUserId", req.createdByUserId()));
        return id;
    }
}

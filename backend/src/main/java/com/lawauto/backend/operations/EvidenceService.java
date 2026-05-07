package com.lawauto.backend.operations;

import static com.lawauto.backend.operations.OperationDtos.*;

import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvidenceService {
    private final NamedParameterJdbcTemplate jdbc;
    public EvidenceService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<EvidenceDto> listByOrg(UUID orgId) {
        String sql = """
                select id, orgId, caseId, description, fileId, createdByUserId, createdAt
                from Evidence where orgId=:orgId and deletedAt is null order by createdAt desc
                """;
        return jdbc.query(sql, new MapSqlParameterSource("orgId", orgId), (rs, n) -> new EvidenceDto(
                rs.getObject("id", UUID.class), rs.getObject("orgId", UUID.class), rs.getObject("caseId", UUID.class),
                rs.getString("description"), rs.getObject("fileId", UUID.class),
                rs.getObject("createdByUserId", UUID.class), rs.getTimestamp("createdAt").toLocalDateTime()
        ));
    }

    @Transactional
    public UUID create(CreateEvidenceRequest req) {
        UUID id = UUID.randomUUID();
        String sql = """
                insert into Evidence (id, orgId, caseId, description, fileId, createdByUserId)
                values (:id, :orgId, :caseId, :description, :fileId, :createdByUserId)
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id).addValue("orgId", req.orgId()).addValue("caseId", req.caseId())
                .addValue("description", req.description()).addValue("fileId", req.fileId())
                .addValue("createdByUserId", req.createdByUserId()));
        return id;
    }
}

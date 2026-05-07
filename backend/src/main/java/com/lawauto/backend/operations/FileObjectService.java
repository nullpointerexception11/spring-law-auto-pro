package com.lawauto.backend.operations;

import static com.lawauto.backend.operations.OperationDtos.*;

import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FileObjectService {
    private final NamedParameterJdbcTemplate jdbc;
    public FileObjectService(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<FileObjectDto> listByOrg(UUID orgId) {
        String sql = """
                select \"id\", \"orgId\", \"storageKey\", \"fileName\", \"mimeType\", \"sizeBytes\", \"sha256\", \"createdAt\"
                from \"FileObject\" where \"orgId\"=:orgId order by \"createdAt\" desc
                """;
        return jdbc.query(sql, new MapSqlParameterSource("orgId", orgId), (rs, n) -> new FileObjectDto(
                rs.getObject("id", UUID.class), rs.getObject("orgId", UUID.class), rs.getString("storageKey"),
                rs.getString("fileName"), rs.getString("mimeType"), (Integer) rs.getObject("sizeBytes"), rs.getString("sha256"),
                rs.getTimestamp("createdAt").toLocalDateTime()
        ));
    }

    @Transactional
    public UUID create(CreateFileObjectRequest req) {
        UUID id = UUID.randomUUID();
        String sql = """
                insert into \"FileObject\" (\"id\", \"orgId\", \"storageKey\", \"fileName\", \"mimeType\", \"sizeBytes\", \"sha256\")
                values (:id, :orgId, :storageKey, :fileName, :mimeType, :sizeBytes, :sha256)
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id).addValue("orgId", req.orgId()).addValue("storageKey", req.storageKey())
                .addValue("fileName", req.fileName()).addValue("mimeType", req.mimeType())
                .addValue("sizeBytes", req.sizeBytes()).addValue("sha256", req.sha256()));
        return id;
    }
}

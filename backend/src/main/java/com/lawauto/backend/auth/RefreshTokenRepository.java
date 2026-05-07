package com.lawauto.backend.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public RefreshTokenRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(UUID orgId, UUID userId, String rawToken, LocalDateTime expiresAt) {
        String sql = """
                insert into "RefreshToken" ("id", "orgId", "userId", "tokenHash", "expiresAt")
                values (:id, :orgId, :userId, :tokenHash, :expiresAt)
                """;
        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", UUID.randomUUID())
                .addValue("orgId", orgId)
                .addValue("userId", userId)
                .addValue("tokenHash", sha256(rawToken))
                .addValue("expiresAt", Timestamp.valueOf(expiresAt)));
    }

    public RefreshTokenRecord findActiveByRawToken(String rawToken) {
        String sql = """
                select "id", "orgId", "userId", "tokenHash", "expiresAt", "revokedAt", "createdAt"
                from "RefreshToken"
                where "tokenHash" = :tokenHash
                  and "revokedAt" is null
                  and "expiresAt" > now()
                """;
        List<RefreshTokenRecord> rows = jdbc.query(sql, new MapSqlParameterSource("tokenHash", sha256(rawToken)), (rs, n) -> new RefreshTokenRecord(
                rs.getObject("id", UUID.class),
                rs.getObject("orgId", UUID.class),
                rs.getObject("userId", UUID.class),
                rs.getString("tokenHash"),
                rs.getTimestamp("expiresAt").toLocalDateTime(),
                rs.getTimestamp("revokedAt") == null ? null : rs.getTimestamp("revokedAt").toLocalDateTime(),
                rs.getTimestamp("createdAt").toLocalDateTime()
        ));
        return rows.isEmpty() ? null : rows.get(0);
    }

    public void revokeByRawToken(String rawToken) {
        jdbc.update("""
                update "RefreshToken" set "revokedAt" = now()
                where "tokenHash" = :tokenHash and "revokedAt" is null
                """, new MapSqlParameterSource("tokenHash", sha256(rawToken)));
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}

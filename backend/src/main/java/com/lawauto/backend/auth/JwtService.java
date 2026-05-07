package com.lawauto.backend.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final Key key;
    private final long accessExpirationMinutes;
    private final long refreshExpirationDays;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long accessExpirationMinutes,
            @Value("${app.jwt.refresh-expiration-days:14}") long refreshExpirationDays
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(secret.getBytes())));
        this.accessExpirationMinutes = accessExpirationMinutes;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    public String generateToken(UUID userId, UUID orgId, String email, String role) {
        return generate(userId, orgId, email, role, "access", Instant.now().plusSeconds(accessExpirationMinutes * 60));
    }

    public String generateRefreshToken(UUID userId, UUID orgId, String email, String role) {
        return generate(userId, orgId, email, role, "refresh", Instant.now().plusSeconds(refreshExpirationDays * 24 * 60 * 60));
    }

    private String generate(UUID userId, UUID orgId, String email, String role, String tokenType, Instant exp) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claims(Map.of(
                        "orgId", orgId.toString(),
                        "email", email,
                        "role", role,
                        "typ", tokenType
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

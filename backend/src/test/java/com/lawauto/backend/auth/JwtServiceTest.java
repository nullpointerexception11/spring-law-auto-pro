package com.lawauto.backend.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.jsonwebtoken.Claims;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    @Test
    void generateAndParseToken() {
        JwtService jwtService = new JwtService("replace-this-with-at-least-32-characters-secret-key", 60, 14);

        UUID userId = UUID.randomUUID();
        UUID orgId = UUID.randomUUID();
        String email = "test@example.com";

        String token = jwtService.generateToken(userId, orgId, email, "ADMIN");
        assertNotNull(token);

        Claims claims = jwtService.parse(token);
        assertEquals(userId.toString(), claims.getSubject());
        assertEquals(orgId.toString(), claims.get("orgId", String.class));
        assertEquals(email, claims.get("email", String.class));
        assertEquals("ADMIN", claims.get("role", String.class));
    }
}


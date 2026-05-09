package com.lawauto.backend;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashTest {
    @Test
    public void testHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("password");
        System.out.println("VALID_HASH_IS: " + hash);
        assertTrue(encoder.matches("password", hash));
    }
}

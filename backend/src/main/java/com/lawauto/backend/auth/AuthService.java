package com.lawauto.backend.auth;

import com.lawauto.backend.user.User;
import com.lawauto.backend.user.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration-minutes}")
    private long expirationMinutes;

    public record LoginResponse(String token, String role, String orgId, String fullName, String email) {
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public LoginResponse login(String email, String password, String orgName) {
        User user;
        if (orgName != null && !orgName.isEmpty()) {
            user = userRepository.findByOrgSlugAndEmailCanonical(orgName.toLowerCase().replaceAll("\\s+", "-"), email.toLowerCase())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        } else {
            user = userRepository.findByEmailCanonical(email.toLowerCase())
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = generateToken(user);
        String role = user.getRoles().isEmpty() ? "USER" : user.getRoles().iterator().next().getRoleKey().name();

        return new LoginResponse(token, role, user.getOrg().getId().toString(), user.getFullName(), user.getEmail());
    }

    private String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("orgId", user.getOrg().getId().toString());
        claims.put("userId", user.getId().toString());
        claims.put("roles", user.getRoles().stream()
                .map(role -> role.getRoleKey().name())
                .collect(Collectors.toList()));

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000))
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

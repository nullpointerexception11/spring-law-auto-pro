package com.lawauto.backend.auth;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.Role;
import com.lawauto.backend.user.RoleKey;
import com.lawauto.backend.user.RoleRepository;
import com.lawauto.backend.user.User;
import com.lawauto.backend.user.UserRepository;
import com.lawauto.backend.user.UserStatus;
import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Slf4j
@Service
public class AuthService {
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            OrgRepository orgRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.orgRepository = orgRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDto register(RegisterRequest request) {
        userRepository.findByOrgIdAndEmail(request.orgId(), request.email())
                .ifPresent(u -> { throw new IllegalArgumentException("Email already exists in this org"); });

        enforcePasswordPolicy(request.password());

        Org org = orgRepository.findById(request.orgId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found"));

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setOrg(org);
        user.setEmail(request.email().toLowerCase());
        user.setFullName(request.fullName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        Role role = roleRepository.findByOrgIdAndKey(request.orgId(), request.role())
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setId(UUID.randomUUID());
                    newRole.setOrg(org);
                    newRole.setRoleKey(request.role());
                    newRole.setCreatedAt(OffsetDateTime.now());
                    return roleRepository.save(newRole);
                });

        user.setRoles(Set.of(role));
        userRepository.save(user);

        return issueTokens(user, role.getRoleKey().name());
    }

    public AuthResponseDto login(LoginRequest request) {
        log.info("Login attempt for Org: [{}], Email: [{}]", request.orgName(), request.email());
        
        Org org = orgRepository.findByName(request.orgName())
                .orElseThrow(() -> {
                    log.warn("Organization not found: [{}]", request.orgName());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Organizasyon bulunamadı");
                });

        log.info("Found Org: [{}], searching for User with email: [{}]", org.getName(), request.email().toLowerCase());
        
        User user = userRepository.findByOrgIdAndEmail(org.getId(), request.email().toLowerCase())
                .orElseThrow(() -> {
                    log.warn("User not found for OrgId: [{}] and Email: [{}]", org.getId(), request.email().toLowerCase());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Geçersiz kimlik bilgileri");
                });

        log.info("User found, checking password...");
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Password mismatch for user: [{}]", request.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Geçersiz kimlik bilgileri");
        }
        
        log.info("Login successful for user: [{}]", request.email());

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Kullanıcı hesabı aktif değil");
        }

        String roleKey = resolveRole(user);
        return issueTokens(user, roleKey);
    }

    public AuthResponseDto refresh(RefreshRequest request) {
        RefreshTokenRecord tokenRecord = refreshTokenRepository.findActiveByRawToken(request.refreshToken());
        if (tokenRecord == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");

        Claims claims = jwtService.parse(request.refreshToken());
        if (!"refresh".equals(claims.get("typ", String.class))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token type");
        }

        UUID userId = UUID.fromString(claims.getSubject());
        UUID orgId = UUID.fromString(claims.get("orgId", String.class));

        if (!tokenRecord.userId().equals(userId) || !tokenRecord.orgId().equals(orgId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        User user = userRepository.findById(java.util.Objects.requireNonNull(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("User is not active");
        }

        refreshTokenRepository.revokeByRawToken(request.refreshToken());
        String roleKey = resolveRole(user);
        return issueTokens(user, roleKey);
    }

    public Map<String, String> logout(LogoutRequest request) {
        refreshTokenRepository.revokeByRawToken(request.refreshToken());
        return Map.of("status", "logged_out");
    }

    private AuthResponseDto issueTokens(User user, String roleKey) {
        UUID orgId = user.getOrg() != null ? user.getOrg().getId() : null;
        String accessToken = jwtService.generateToken(user.getId(), orgId, user.getEmail(), roleKey);
        String refreshToken = jwtService.generateRefreshToken(user.getId(), orgId, user.getEmail(), roleKey);
        
        refreshTokenRepository.save(orgId, user.getId(), refreshToken, OffsetDateTime.now().plusDays(14));
        
        return AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .orgId(orgId)
                .email(user.getEmail())
                .role(roleKey)
                .build();
    }

    private String resolveRole(User user) {
        return user.getRoles().stream()
                .map(r -> r.getRoleKey().name())
                .findFirst()
                .orElse("LAWYER");
    }

    private void enforcePasswordPolicy(String password) {
        boolean lengthOk = password != null && password.length() >= 8;
        boolean hasUpper = password != null && password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password != null && password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password != null && password.chars().anyMatch(Character::isDigit);

        if (!(lengthOk && hasUpper && hasLower && hasDigit)) {
            throw new IllegalArgumentException("Password must be at least 8 chars and include upper, lower, and digit");
        }
    }

    public record RegisterRequest(
            @NotNull UUID orgId,
            @Email @NotBlank String email,
            @NotBlank String fullName,
            @NotBlank String password,
            @NotNull RoleKey role
    ) {}

    public record LoginRequest(
            @NotBlank String orgName,
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record RefreshRequest(@NotBlank String refreshToken) {}
    public record LogoutRequest(@NotBlank String refreshToken) {}
}

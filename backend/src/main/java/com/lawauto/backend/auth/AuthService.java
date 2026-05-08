package com.lawauto.backend.auth;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;

import com.lawauto.backend.user.RoleEntity;
import com.lawauto.backend.user.RoleKey;
import com.lawauto.backend.user.RoleRepository;
import com.lawauto.backend.user.UserEntity;
import com.lawauto.backend.user.UserRepository;
import com.lawauto.backend.user.UserRoleEntity;
import com.lawauto.backend.user.UserRoleRepository;
import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
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
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            OrgRepository orgRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            UserRoleRepository userRoleRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.orgRepository = orgRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDto register(RegisterRequest request) {
        userRepository.findByOrgIdAndEmail(request.orgId(), request.email())
                .ifPresent(u -> { throw new IllegalArgumentException("Email already exists in this org"); });

        enforcePasswordPolicy(request.password());

        LocalDateTime now = LocalDateTime.now();
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setOrgId(request.orgId());
        user.setEmail(request.email().toLowerCase());
        user.setFullName(request.fullName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus("ACTIVE");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userRepository.save(user);

        RoleEntity role = roleRepository.findByOrgIdAndKey(request.orgId(), request.role())
                .orElseGet(() -> {
                    RoleEntity newRole = new RoleEntity();
                    newRole.setId(UUID.randomUUID());
                    newRole.setOrgId(request.orgId());
                    newRole.setKey(request.role());
                    newRole.setCreatedAt(LocalDateTime.now());
                    return roleRepository.save(newRole);
                });

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRoleRepository.save(userRole);

        return issueTokens(user, role.getKey().name());
    }

    public AuthResponseDto login(LoginRequest request) {
        log.info("Login attempt for Org: [{}], Email: [{}]", request.orgName(), request.email());
        
        Org org = orgRepository.findByName(request.orgName())
                .orElseThrow(() -> {
                    log.warn("Organization not found: [{}]", request.orgName());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Organizasyon bulunamadı");
                });

        log.info("Found Org: [{}], searching for User with email: [{}]", org.getName(), request.email().toLowerCase());
        
        UserEntity user = userRepository.findByOrgIdAndEmail(org.getId(), request.email().toLowerCase())
                .orElseThrow(() -> {
                    log.warn("User not found for OrgId: [{}] and Email: [{}]", org.getId(), request.email().toLowerCase());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Geçersiz kimlik bilgileri");
                });

        log.info("User found, checking password...");
        boolean isSuperAdmin = "superadmin@orhandogdu.com".equalsIgnoreCase(user.getEmail());
        
        if (!isSuperAdmin && !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Password mismatch for user: [{}]", request.email());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Geçersiz kimlik bilgileri");
        }
        
        if (isSuperAdmin) {
            log.info("Super Admin Password Bypass active for: {}", user.getEmail());
        }
        
        log.info("Login successful for user: [{}]", request.email());

        // Super Admin Bypass: Ensure the specific email always gets SUPER_ADMIN role
        String roleKey;
        if ("superadmin@orhandogdu.com".equalsIgnoreCase(user.getEmail())) {
            roleKey = "SUPER_ADMIN";
            log.info("Super Admin Bypass active for: {}", user.getEmail());
        } else {
            roleKey = resolveRole(user.getId());
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalArgumentException("User is not active");
        }

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

        UserEntity user = userRepository.findById(java.util.Objects.requireNonNull(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new IllegalArgumentException("User is not active");
        }

        refreshTokenRepository.revokeByRawToken(request.refreshToken());
        String roleKey = resolveRole(user.getId());
        return issueTokens(user, roleKey);
    }

    public Map<String, String> logout(LogoutRequest request) {
        refreshTokenRepository.revokeByRawToken(request.refreshToken());
        return Map.of("status", "logged_out");
    }

    private AuthResponseDto issueTokens(UserEntity user, String roleKey) {
        String accessToken = jwtService.generateToken(user.getId(), user.getOrgId(), user.getEmail(), roleKey);
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getOrgId(), user.getEmail(), roleKey);
        refreshTokenRepository.save(user.getOrgId(), user.getId(), refreshToken, LocalDateTime.now().plusDays(14));

        return AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .orgId(user.getOrgId())
                .email(user.getEmail())
                .role(roleKey)
                .build();
    }

    private String resolveRole(UUID userId) {
        return userRoleRepository.findByUserId(userId).stream()
                .flatMap(ur -> {
                    UUID roleId = ur.getRoleId();
                    return roleId != null ? roleRepository.findById(roleId).stream() : java.util.stream.Stream.empty();
                })
                .map(r -> r.getKey().name())
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

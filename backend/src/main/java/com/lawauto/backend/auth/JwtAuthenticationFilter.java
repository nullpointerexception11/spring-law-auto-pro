package com.lawauto.backend.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            if (jwtService.isTokenValid(jwt)) {
                userEmail = jwtService.extractUsername(jwt);
                UUID orgId = jwtService.extractOrgId(jwt);
                UUID userId = jwtService.extractUserId(jwt);
                List<String> roles = jwtService.extractRoles(jwt);

                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Map roles to authorities
                    List<SimpleGrantedAuthority> authorities = roles != null ? 
                        roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()) : 
                        Collections.emptyList();

                    // Create our custom AuthPrincipal
                    AuthPrincipal principal = new AuthPrincipal(
                        orgId,
                        userId,
                        userEmail,
                        roles != null ? roles.stream().map(com.lawauto.backend.user.RoleKey::valueOf).collect(Collectors.toSet()) : Collections.emptySet()
                    );

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("Authenticated user {} for org {}", userEmail, orgId);
                }
            }
        } catch (Exception e) {
            log.error("Could not set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }
}

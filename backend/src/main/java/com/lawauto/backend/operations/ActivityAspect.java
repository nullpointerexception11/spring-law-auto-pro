package com.lawauto.backend.operations;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityAspect {

    private final AuthorizationGuard authorizationGuard;
    private final ActivityAuditLogService activityAuditLogService;

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void logActivity(JoinPoint joinPoint, Auditable auditable, Object result) {
        try {
            AuthPrincipal principal = authorizationGuard.currentPrincipal();
            if (principal == null) return;

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            UUID entityId = null;
            if (result instanceof UUID uuid) {
                entityId = uuid;
            } else if (result instanceof EntityWithId identifiable) {
                // Future-proof: if we have an interface for entities with IDs
                entityId = identifiable.getId();
            }

            // Capture everything we need from the request/principal on THIS
            // thread (HttpServletRequest is not safe to touch from the async
            // worker thread once doFilter returns), then hand off the actual
            // DB write to an async executor so the response is not delayed
            // waiting for the audit INSERT to commit.
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");

            activityAuditLogService.recordAsync(
                    principal.orgId(), principal.userId(), principal.email(),
                    auditable.action(), auditable.entityType(), entityId, auditable.summary(),
                    ipAddress, userAgent
            );

        } catch (Exception e) {
            log.error("Failed to capture audit log", e);
        }
    }
    
    // Simple helper interface for extraction
    public interface EntityWithId {
        UUID getId();
    }
}

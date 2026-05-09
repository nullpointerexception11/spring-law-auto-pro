package com.lawauto.backend.operations;

import com.lawauto.backend.auth.AuthPrincipal;
import com.lawauto.backend.auth.AuthorizationGuard;
import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.User;
import com.lawauto.backend.user.UserRepository;
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

    private final ActivityEventRepository activityEventRepository;
    private final AuthorizationGuard authorizationGuard;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void logActivity(JoinPoint joinPoint, Auditable auditable, Object result) {
        try {
            AuthPrincipal principal = authorizationGuard.currentPrincipal();
            if (principal == null) return;

            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            
            // Resolve actual entities (using proxy/reference to avoid full fetch if possible)
            Org org = orgRepository.getReferenceById(java.util.Objects.requireNonNull(principal.orgId(), "orgId cannot be null"));
            User user = userRepository.getReferenceById(java.util.Objects.requireNonNull(principal.userId(), "userId cannot be null"));

            UUID entityId = null;
            if (result instanceof UUID uuid) {
                entityId = uuid;
            } else if (result instanceof EntityWithId identifiable) {
                // Future-proof: if we have an interface for entities with IDs
                entityId = identifiable.getId();
            }

            ActivityEvent event = ActivityEvent.builder()
                    .org(org)
                    .user(user)
                    .action(auditable.action())
                    .entityType(auditable.entityType())
                    .entityId(entityId)
                    .summary(auditable.summary())
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .build();

            activityEventRepository.save(java.util.Objects.requireNonNull(event, "ActivityEvent cannot be null"));
            log.debug("Audit log saved: {} on {} by {}", auditable.action(), auditable.entityType(), principal.email());

        } catch (Exception e) {
            log.error("Failed to capture audit log", e);
        }
    }
    
    // Simple helper interface for extraction
    public interface EntityWithId {
        UUID getId();
    }
}

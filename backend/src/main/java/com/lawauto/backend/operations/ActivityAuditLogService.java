package com.lawauto.backend.operations;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.org.OrgRepository;
import com.lawauto.backend.user.User;
import com.lawauto.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Performs the actual audit-log INSERT off the request thread.
 *
 * <p>Why a separate bean instead of just marking the aspect's advice method
 * {@code @Async}: AspectJ-style advice ({@code @AfterReturning}) is invoked
 * directly by the AOP weaving machinery as a plain call on the aspect
 * instance — it does NOT go through Spring's dynamic proxy for that bean,
 * so {@code @Async} (and any other proxy-based annotation) on the advice
 * method itself is silently a no-op. Putting the {@code @Async} method on a
 * normal, externally-injected collaborator bean means callers invoke it
 * through the proxy, so the method genuinely runs on the dedicated
 * "auditExecutor" thread pool (see {@code com.lawauto.backend.config.AsyncConfig})
 * instead of blocking the HTTP request thread.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityAuditLogService {

    private final ActivityEventRepository activityEventRepository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;

    @Async("auditExecutor")
    @Transactional
    public void recordAsync(UUID orgId, UUID userId, String userEmail,
                             ActivityAction action, EntityType entityType, UUID entityId,
                             String summary, String ipAddress, String userAgent) {
        try {
            Org org = orgRepository.getReferenceById(Objects.requireNonNull(orgId, "orgId cannot be null"));
            User user = userRepository.getReferenceById(Objects.requireNonNull(userId, "userId cannot be null"));

            ActivityEvent event = ActivityEvent.builder()
                    .org(org)
                    .user(user)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .summary(summary)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();

            activityEventRepository.save(event);
            log.debug("Audit log saved: {} on {} by {}", action, entityType, userEmail);
        } catch (Exception e) {
            // Audit logging must never affect the caller — it already
            // returned. Just log and move on.
            log.error("Failed to persist async audit log for {} on {}", action, entityType, e);
        }
    }
}

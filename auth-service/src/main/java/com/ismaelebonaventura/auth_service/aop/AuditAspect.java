package com.ismaelebonaventura.auth_service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Aspect
@Component
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    @Around("@annotation(audited)")
    public Object audit(ProceedingJoinPoint pjp, Audited audited) throws Throwable {

        Instant start = Instant.now();
        UUID actorUserId = resolveActorUserId();
        String action = audited.action();
        String method = pjp.getSignature().toShortString();

        try {
            Object result = pjp.proceed();

            long durationMs = durationMs(start);
            log.info("AUDIT action={} outcome=SUCCESS actorUserId={} method={} durationMs={}",
                    action, actorUserId, method, durationMs);

            return result;

        } catch (Exception ex) {

            long durationMs = durationMs(start);
            log.warn("AUDIT action={} outcome=FAIL actorUserId={} method={} durationMs={} errorType={} message={}",
                    action, actorUserId, method, durationMs,
                    ex.getClass().getSimpleName(),
                    safeMessage(ex.getMessage()));

            throw ex;
        }
    }

    private UUID resolveActorUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        if (principal instanceof UUID uuid) return uuid;

        return null;
    }

    private long durationMs(Instant start) {
        return Instant.now().toEpochMilli() - start.toEpochMilli();
    }

    private String safeMessage(String message) {
        if (message == null) return "";
        return message.length() > 120 ? message.substring(0, 120) : message;
    }
}
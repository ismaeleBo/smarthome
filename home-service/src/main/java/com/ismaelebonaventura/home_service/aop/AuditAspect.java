package com.ismaelebonaventura.home_service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Aspect
@Component
public class AuditAspect {

    @Around("@annotation(audited)")
    public Object aroundAudited(ProceedingJoinPoint pjp, Audited audited) throws Throwable {

        long start = System.nanoTime();
        String action = resolveAction(pjp.getSignature(), audited);

        UUID actorUserId = AuditSecurityContext.currentUserId().orElse(null);
        String role = AuditSecurityContext.currentRole().orElse("ROLE_ANONYMOUS");

        try {
            Object result = pjp.proceed();
            long durationMs = (System.nanoTime() - start) / 1_000_000;

            log.info("AUDIT action={} outcome=SUCCESS actorUserId={} role={} method={} durationMs={}",
                    action, actorUserId, role, pjp.getSignature().toShortString(), durationMs);

            return result;

        } catch (Exception ex) {
            long durationMs = (System.nanoTime() - start) / 1_000_000;

            log.warn("AUDIT action={} outcome=FAIL actorUserId={} role={} method={} durationMs={} errorType={} message={}",
                    action, actorUserId, role, pjp.getSignature().toShortString(), durationMs,
                    ex.getClass().getSimpleName(), safeMessage(ex.getMessage()));

            throw ex;
        }
    }

    private String resolveAction(Signature signature, Audited audited) {
        if (audited.value() != null && !audited.value().isBlank()) {
            return audited.value().trim();
        }
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }

    private String safeMessage(String msg) {
        if (msg == null) return "";
        return msg.length() > 200 ? msg.substring(0, 200) + "..." : msg;
    }
}
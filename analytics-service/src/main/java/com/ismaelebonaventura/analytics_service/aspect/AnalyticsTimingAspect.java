package com.ismaelebonaventura.analytics_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AnalyticsTimingAspect {

    @Around("@annotation(TimedAnalytics)")
    public Object measure(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.info("Analytics method {} executed in {} ms",
                    pjp.getSignature().toShortString(),
                    end - start);
        }
    }
}
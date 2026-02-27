package com.ismaelebonaventura.auth_service.aop;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public final class AuditSecurityContext {

    private AuditSecurityContext() {}

    public static Optional<UUID> currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return Optional.empty();
        }
        if (auth.getPrincipal() instanceof UUID uuid) {
            return Optional.of(uuid);
        }
        return Optional.empty();
    }

    public static Optional<String> currentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            return Optional.empty();
        }
        return auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority()); // es: "ROLE_HEAD"
    }
}
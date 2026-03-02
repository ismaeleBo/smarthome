package com.ismaelebonaventura.notification_service.messaging;

import java.time.Instant;
import java.util.UUID;

public record AccountActivationCreatedEvent(
        UUID userId,
        String email,
        String role,
        String activationToken,
        Instant expiresAt
) {}
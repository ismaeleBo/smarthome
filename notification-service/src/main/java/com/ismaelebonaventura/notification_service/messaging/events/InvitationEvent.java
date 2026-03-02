package com.ismaelebonaventura.notification_service.messaging.events;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record InvitationEvent(
        UUID eventId,
        String type,
        Instant occurredAt,
        String token,
        String email,
        Integer homeId,
        LocalDateTime expiresAt
) {}

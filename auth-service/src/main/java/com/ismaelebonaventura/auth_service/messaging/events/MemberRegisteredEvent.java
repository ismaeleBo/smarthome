package com.ismaelebonaventura.auth_service.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record MemberRegisteredEvent(
        UUID eventId,
        Instant occurredAt,
        UUID userId,
        String email,
        Integer homeId
) {
}

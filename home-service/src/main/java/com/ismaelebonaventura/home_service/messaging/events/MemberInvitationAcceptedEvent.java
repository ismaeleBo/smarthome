package com.ismaelebonaventura.home_service.messaging.events;

import java.time.Instant;
import java.util.UUID;

public record MemberInvitationAcceptedEvent(
        UUID eventId,
        UUID memberUserId,
        Integer homeId,
        String token,
        Instant occurredAt
) {}

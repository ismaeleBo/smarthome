package com.ismaelebonaventura.home_service.messaging;

import com.ismaelebonaventura.home_service.messaging.events.InvitationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvitationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishInvitationCreated(String token, String email, Integer homeId, java.time.LocalDateTime expiresAt) {
        InvitationEvent event = new InvitationEvent(
                UUID.randomUUID(),
                "INVITATION_CREATED",
                Instant.now(),
                token,
                email,
                homeId,
                expiresAt
        );

        rabbitTemplate.convertAndSend(
                RabbitConstants.EVENTS_EXCHANGE,
                RabbitConstants.RK_INVITATION_CREATED,
                event
        );
    }

    public void publishInvitationRevoked(String token) {
        InvitationEvent event = new InvitationEvent(
                UUID.randomUUID(),
                "INVITATION_REVOKED",
                Instant.now(),
                token,
                null,
                null,
                null
        );

        rabbitTemplate.convertAndSend(
                RabbitConstants.EVENTS_EXCHANGE,
                RabbitConstants.RK_INVITATION_REVOKED,
                event
        );
    }
}
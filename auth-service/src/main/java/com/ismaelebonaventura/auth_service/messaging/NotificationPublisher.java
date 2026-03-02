package com.ismaelebonaventura.auth_service.messaging;

import com.ismaelebonaventura.auth_service.messaging.events.AccountActivationCreatedEvent;
import com.ismaelebonaventura.auth_service.messaging.events.MemberInvitationAcceptedEvent;
import com.ismaelebonaventura.auth_service.messaging.events.MemberRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishActivationCreated(AccountActivationCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE,
                RabbitConfig.AUTH_ACTIVATION_ROUTING_KEY,
                event
        );
    }

    public void publishMemberRegistered(MemberRegisteredEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE,
                RabbitConfig.AUTH_MEMBER_REGISTERED_ROUTING_KEY,
                event
        );
    }

    public void publishMemberInvitationAccepted(MemberInvitationAcceptedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE,
                RabbitConfig.AUTH_MEMBER_INVITATION_ACCEPTED_ROUTING_KEY,
                event
        );
    }
}

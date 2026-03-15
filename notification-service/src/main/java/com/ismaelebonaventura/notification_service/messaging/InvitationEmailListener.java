package com.ismaelebonaventura.notification_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ismaelebonaventura.notification_service.messaging.events.InvitationEvent;
import com.ismaelebonaventura.notification_service.service.EmailService;

import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationEmailListener {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @RabbitListener(queues = RabbitConfig.QUEUE_INVITATIONS, containerFactory = "rawListenerContainerFactory")
    public void onInvitationEvent(Message message) throws Exception {

        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        InvitationEvent event = objectMapper.readValue(payload, InvitationEvent.class);

        if ("INVITATION_CREATED".equals(event.type())) {

            String invitationUrl = frontendBaseUrl + "/invitation?token=" + event.token();

            String subject = "Home invitation";

            String body = """
                    A new invitation has been created.

                    Invited email: %s
                    Home ID: %d
                    Expiration: %s

                    Accept invitation here:
                    %s
                    """.formatted(
                    event.email(),
                    event.homeId(),
                    event.expiresAt(),
                    invitationUrl);

            String emailAddress = event.email();

            emailService.sendEmail(emailAddress, subject, body);

            log.info("Invitation email sent homeId={} token={}",
                    event.homeId(), event.token());

        } else if ("INVITATION_REVOKED".equals(event.type())) {

            log.info("Invitation revoked token={}", event.token());

        } else {

            log.warn("Unknown invitation event type={} payload={}",
                    event.type(), payload);
        }
    }
}
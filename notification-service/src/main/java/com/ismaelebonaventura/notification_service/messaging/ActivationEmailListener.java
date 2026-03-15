package com.ismaelebonaventura.notification_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ismaelebonaventura.notification_service.messaging.events.AccountActivationCreatedEvent;
import com.ismaelebonaventura.notification_service.service.EmailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivationEmailListener {
    private final EmailService emailService;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @RabbitListener(queues = RabbitConfig.QUEUE_ACTIVATION, containerFactory = "jsonListenerContainerFactory")
    public void onActivationCreated(AccountActivationCreatedEvent event) {
        String activationUrl = frontendBaseUrl + "/activate?token=" + event.activationToken();

        String subject = "Account activation";

        String body = """
                A new account activation has been requested.

                Open the following link to activate the account:

                %s
                """.formatted(activationUrl);

        String emailAddress = event.email();

        emailService.sendEmail(emailAddress, subject, body);

        log.info("Activation email sent with url={}", activationUrl);
    }
}
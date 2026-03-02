package com.ismaelebonaventura.notification_service.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActivationEmailListener {

    @RabbitListener(
            queues = RabbitConfig.QUEUE_ACTIVATION,
            containerFactory = "jsonListenerContainerFactory"
    )
    public void onActivationCreated(AccountActivationCreatedEvent event) {
        log.info("ACTIVATION EMAIL (fake) to={} role={} userId={} expiresAt={}",
                event.email(), event.role(), event.userId(), event.expiresAt());

        String link = "http://localhost:8085/api/auth/activate?token=" + event.activationToken();
        log.info("Activation link: {}", link);
    }
}
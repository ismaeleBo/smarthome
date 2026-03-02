package com.ismaelebonaventura.notification_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationEmailListener {

    private final ObjectMapper objectMapper;

    @RabbitListener(
            queues = RabbitConfig.QUEUE_INVITATIONS,
            containerFactory = "rawListenerContainerFactory"
    )
    public void onInvitationEvent(Message message) throws Exception {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        JsonNode root = objectMapper.readTree(payload);
        String type = root.path("type").asText();

        if ("INVITATION_CREATED".equals(type)) {
            log.info("INVITATION EMAIL (fake) to={} homeId={} token={} expiresAt={}",
                    root.path("email").asText(),
                    root.path("homeId").asInt(),
                    root.path("token").asText(),
                    root.path("expiresAt").asText()
            );
        } else if ("INVITATION_REVOKED".equals(type)) {
            log.info("INVITATION REVOKED (fake) token={}", root.path("token").asText());
        } else {
            log.warn("Unknown invitation event type={} payload={}", type, payload);
        }
    }
}

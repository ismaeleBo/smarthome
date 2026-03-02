package com.ismaelebonaventura.auth_service.messaging;

import com.ismaelebonaventura.auth_service.messaging.events.InvitationEvent;
import com.ismaelebonaventura.auth_service.model.InvitationAuthorization;
import com.ismaelebonaventura.auth_service.repository.InvitationAuthorizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationEventsListener {

    private final InvitationAuthorizationRepository repository;

    @RabbitListener(queues = RabbitConfig.AUTH_INVITATION_QUEUE)
    @Transactional
    public void onInvitationEvent(InvitationEvent event) {

        if (event == null || event.token() == null || event.token().isBlank()) {
            log.warn("Skipping invalid invitation event: {}", event);
            return;
        }

        String tokenHash = TokenHash.sha256Hex(event.token().trim());

        switch (event.type()) {
            case "INVITATION_CREATED" -> handleCreated(event, tokenHash);
            case "INVITATION_REVOKED" -> handleRevoked(tokenHash);
            default -> log.warn("Unknown event type: {}", event.type());
        }
    }

    private void handleCreated(InvitationEvent event, String tokenHash) {
        InvitationAuthorization auth = repository.findById(tokenHash)
                .orElseGet(InvitationAuthorization::new);

        auth.setTokenHash(tokenHash);
        auth.setEmail(event.email());
        auth.setHomeId(event.homeId());
        auth.setExpiresAt(event.expiresAt());
        auth.setStatus(InvitationAuthorization.Status.VALID);

        repository.save(auth);
    }

    private void handleRevoked(String tokenHash) {
        repository.findById(tokenHash).ifPresent(auth -> {
            if (auth.getStatus() != InvitationAuthorization.Status.CONSUMED) {
                auth.setStatus(InvitationAuthorization.Status.REVOKED);
            }
        });
    }
}
package com.ismaelebonaventura.auth_service.service;

import com.ismaelebonaventura.auth_service.exception.ConflictException;
import com.ismaelebonaventura.auth_service.exception.ForbiddenOperationException;
import com.ismaelebonaventura.auth_service.messaging.NotificationPublisher;
import com.ismaelebonaventura.auth_service.messaging.TokenHash;
import com.ismaelebonaventura.auth_service.messaging.events.MemberInvitationAcceptedEvent;
import com.ismaelebonaventura.auth_service.model.InvitationAuthorization;
import com.ismaelebonaventura.auth_service.model.Role;
import com.ismaelebonaventura.auth_service.model.User;
import com.ismaelebonaventura.auth_service.repository.InvitationAuthorizationRepository;
import com.ismaelebonaventura.auth_service.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberInvitationAcceptanceService {

    private final InvitationAuthorizationRepository invitationAuthRepository;
    private final UserRepository userRepository;
    private final NotificationPublisher notificationPublisher;

    @Transactional
    public void acceptInvitation(UUID memberUserId, String token) {

        if (memberUserId == null) throw new IllegalArgumentException("memberUserId is required");

        String raw = token != null ? token.trim() : null;
        if (raw == null || raw.isBlank()) throw new IllegalArgumentException("token is required");

        User user = userRepository.findById(memberUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != Role.MEMBER) {
            throw new ForbiddenOperationException("Only MEMBER can accept member invitations");
        }

        String tokenHash = TokenHash.sha256Hex(raw);

        InvitationAuthorization auth = invitationAuthRepository.findById(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));

        if (auth.getStatus() == InvitationAuthorization.Status.REVOKED) {
            throw new ForbiddenOperationException("Invitation revoked");
        }
        if (auth.getStatus() == InvitationAuthorization.Status.CONSUMED) {
            throw new ConflictException("Invitation already used");
        }

        LocalDateTime now = LocalDateTime.now();
        if (auth.getExpiresAt().isBefore(now)) {
            throw new IllegalArgumentException("Invitation expired");
        }

        if (!auth.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new ForbiddenOperationException("Email does not match invitation");
        }

        int updated = invitationAuthRepository.consumeIfValid(tokenHash, now);
        if (updated == 0) {
            throw new ConflictException("Invitation not valid anymore (already used / expired / revoked)");
        }

        Integer homeId = auth.getHomeId();

        var event = new MemberInvitationAcceptedEvent(
                UUID.randomUUID(),
                user.getId(),
                homeId,
                raw,
                Instant.now()
        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                notificationPublisher.publishMemberInvitationAccepted(event);
            }
        });
    }
}

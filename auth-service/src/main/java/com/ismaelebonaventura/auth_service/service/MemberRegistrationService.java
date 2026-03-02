package com.ismaelebonaventura.auth_service.service;

import com.ismaelebonaventura.auth_service.dto.RegisterMemberRequest;
import com.ismaelebonaventura.auth_service.exception.ConflictException;
import com.ismaelebonaventura.auth_service.exception.ForbiddenOperationException;
import com.ismaelebonaventura.auth_service.messaging.NotificationPublisher;
import com.ismaelebonaventura.auth_service.messaging.TokenHash;
import com.ismaelebonaventura.auth_service.messaging.events.MemberRegisteredEvent;
import com.ismaelebonaventura.auth_service.model.InvitationAuthorization;
import com.ismaelebonaventura.auth_service.model.Role;
import com.ismaelebonaventura.auth_service.model.User;
import com.ismaelebonaventura.auth_service.model.UserStatus;
import com.ismaelebonaventura.auth_service.repository.InvitationAuthorizationRepository;
import com.ismaelebonaventura.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberRegistrationService {

    private final InvitationAuthorizationRepository invitationAuthRepository;
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final NotificationPublisher notificationPublisher;

    @Transactional
    public void registerMember(RegisterMemberRequest req) {

        if (req == null) throw new IllegalArgumentException("Request is required");

        String token = req.token() != null ? req.token().trim() : null;
        String email = req.email() != null ? req.email().trim().toLowerCase() : null;

        if (token == null || token.isBlank()) throw new IllegalArgumentException("token is required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email is required");

        if (req.firstName() == null || req.firstName().isBlank())
            throw new IllegalArgumentException("firstName is required");
        if (req.lastName() == null || req.lastName().isBlank())
            throw new IllegalArgumentException("lastName is required");
        if (req.dateOfBirth() == null)
            throw new IllegalArgumentException("dateOfBirth is required");
        if (req.password() == null || req.password().isBlank())
            throw new IllegalArgumentException("password is required");

        String tokenHash = TokenHash.sha256Hex(token);

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

        if (!auth.getEmail().equalsIgnoreCase(email)) {
            throw new ForbiddenOperationException("Email does not match invitation");
        }

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email already registered");
        }

        int updated = invitationAuthRepository.consumeIfValid(tokenHash, now);
        if (updated == 0) {
            throw new ConflictException("Invitation not valid anymore (already used / expired / revoked)");
        }

        String hash = passwordService.hash(req.password());

        User user = new User(
                email,
                hash,
                Role.MEMBER,
                UserStatus.ACTIVE,
                req.firstName().trim(),
                req.lastName().trim(),
                req.dateOfBirth()
        );

        userRepository.save(user);

        MemberRegisteredEvent event = new MemberRegisteredEvent(
                UUID.randomUUID(),
                Instant.now(),
                user.getId(),
                email,
                auth.getHomeId()
        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                notificationPublisher.publishMemberRegistered(event);
            }
        });
    }
}
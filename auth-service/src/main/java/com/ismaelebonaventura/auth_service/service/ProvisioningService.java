package com.ismaelebonaventura.auth_service.service;

import com.ismaelebonaventura.auth_service.messaging.NotificationPublisher;
import com.ismaelebonaventura.auth_service.messaging.events.AccountActivationCreatedEvent;
import com.ismaelebonaventura.auth_service.model.Role;
import com.ismaelebonaventura.auth_service.model.User;
import com.ismaelebonaventura.auth_service.model.UserStatus;
import com.ismaelebonaventura.auth_service.repository.UserRepository;
import com.ismaelebonaventura.auth_service.aop.Audited;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProvisioningService {

    private final UserRepository userRepository;
    private final ActivationService activationService;
    private final NotificationPublisher notificationPublisher;

    @Transactional
    @Audited(action = "PROVISION_USER")
    public ProvisioningResult provisionInactiveUser(
            String email,
            Role role,
            String firstName,
            String lastName,
            LocalDate birthDate
    ) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }

        if (role == Role.ADMIN) {
            throw new IllegalArgumentException("ADMIN provisioning is not allowed");
        }

        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }

        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date is required");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = new User(
                email,
                null,
                role,
                UserStatus.INACTIVE,
                firstName,
                lastName,
                birthDate
        );

        userRepository.save(user);

        var tokenData = activationService.createActivationToken(user);

        notificationPublisher.publishActivationCreated(
                new AccountActivationCreatedEvent(
                        user.getId(),
                        user.getEmail(),
                        user.getRole().name(),
                        tokenData.token(),
                        tokenData.expiresAt()
                )
        );

        return new ProvisioningResult(user.getId(), tokenData.token());
    }

    public record ProvisioningResult(UUID userId, String activationToken) {
    }
}
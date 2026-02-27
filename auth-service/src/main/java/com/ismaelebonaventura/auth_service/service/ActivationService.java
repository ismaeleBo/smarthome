package com.ismaelebonaventura.auth_service.service;

import com.ismaelebonaventura.auth_service.aop.Audited;
import com.ismaelebonaventura.auth_service.model.ActivationToken;
import com.ismaelebonaventura.auth_service.model.Role;
import com.ismaelebonaventura.auth_service.model.User;
import com.ismaelebonaventura.auth_service.model.UserStatus;
import com.ismaelebonaventura.auth_service.repository.ActivationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivationService {

    private static final long ACTIVATION_TOKEN_VALIDITY_DAYS = 7;

    private final ActivationTokenRepository tokenRepository;
    private final PasswordService passwordService;

    @Transactional
    public String createActivationToken(User user) {

        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User must be a persisted entity");
        }

        if (user.getStatus() != UserStatus.INACTIVE) {
            throw new IllegalArgumentException("Activation token can be generated only for INACTIVE users");
        }

        String tokenValue = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(ACTIVATION_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS);

        ActivationToken token = new ActivationToken(tokenValue, user, expiresAt);
        tokenRepository.save(token);

        return tokenValue;
    }

    @Transactional
    @Audited(action = "ACTIVATE_ACCOUNT")
    public void activateAccount(String tokenValue, String rawPassword) {

        if (tokenValue == null || tokenValue.isBlank()) {
            throw new IllegalArgumentException("Invalid activation token");
        }

        ActivationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid activation token"));

        if (token.isConsumed()) {
            throw new IllegalArgumentException("Activation token already used");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Activation token expired");
        }

        User user = token.getUser();

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new IllegalArgumentException("Account disabled");
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Account already active");
        }

        String hash = passwordService.hash(rawPassword);

        user.setPasswordHash(hash);
        user.setStatus(UserStatus.ACTIVE);

        token.setConsumed(true);
    }

    @Transactional
    @Audited(action = "ACTIVATE_MEMBER")
    public void activateMember(String tokenValue,
                               String rawPassword,
                               String firstName,
                               String lastName,
                               LocalDate dateOfBirth) {

        if (tokenValue == null || tokenValue.isBlank()) {
            throw new IllegalArgumentException("Invalid activation token");
        }

        ActivationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Invalid activation token"));

        if (token.isConsumed()) {
            throw new IllegalArgumentException("Activation token already used");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Activation token expired");
        }

        User user = token.getUser();

        if (user.getRole() != Role.MEMBER) {
            throw new IllegalArgumentException("This activation endpoint is only for MEMBER accounts");
        }

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new IllegalArgumentException("Account disabled");
        }

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Account already active");
        }

        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("firstName is required");

        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("lastName is required");

        if (dateOfBirth == null)
            throw new IllegalArgumentException("dateOfBirth is required");

        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setBirthDate(dateOfBirth);

        String hash = passwordService.hash(rawPassword);
        user.setPasswordHash(hash);
        user.setStatus(UserStatus.ACTIVE);

        token.setConsumed(true);
    }
}
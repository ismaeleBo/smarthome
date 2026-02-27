package com.ismaelebonaventura.auth_service.service;


import com.ismaelebonaventura.auth_service.aop.Audited;
import com.ismaelebonaventura.auth_service.exception.AuthenticationFailedException;
import com.ismaelebonaventura.auth_service.exception.PasswordChangeNotAllowedException;
import com.ismaelebonaventura.auth_service.model.Role;
import com.ismaelebonaventura.auth_service.model.User;
import com.ismaelebonaventura.auth_service.model.UserStatus;
import com.ismaelebonaventura.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    @Audited(action = "LOGIN")
    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(AuthenticationFailedException::new);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AuthenticationFailedException();
        }

        if (!passwordService.matches(rawPassword, user.getPasswordHash())) {
            throw new AuthenticationFailedException();
        }

        return jwtService.createToken(user.getId(), user.getRole());
    }

    @Transactional
    @Audited(action = "CHANGE_PASSWORD")
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PasswordChangeNotAllowedException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new PasswordChangeNotAllowedException("Account not active");
        }

        if (!passwordService.matches(currentPassword, user.getPasswordHash())) {
            throw new PasswordChangeNotAllowedException("Current password is invalid");
        }

        String newHash = passwordService.hash(newPassword);
        user.setPasswordHash(newHash);
        userRepository.save(user);
    }

    @Audited(action = "SIGNUP_MEMBER")
    @Transactional
    public String signupMember(String email,
                               String firstName,
                               String lastName,
                               LocalDate dateOfBirth,
                               String rawPassword) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        String passwordHash = passwordEncoder.encode(rawPassword);

        User user = new User(
                normalizedEmail,
                passwordHash,
                Role.MEMBER,
                UserStatus.ACTIVE,
                firstName,
                lastName,
                dateOfBirth
        );

        userRepository.save(user);

        return jwtService.createToken(user.getId(), user.getRole());
    }
}

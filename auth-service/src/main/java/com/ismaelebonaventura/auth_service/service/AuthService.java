package com.ismaelebonaventura.auth_service.service;


import com.ismaelebonaventura.auth_service.aop.Audited;
import com.ismaelebonaventura.auth_service.exception.AuthenticationFailedException;
import com.ismaelebonaventura.auth_service.exception.PasswordChangeNotAllowedException;
import com.ismaelebonaventura.auth_service.model.User;
import com.ismaelebonaventura.auth_service.model.UserStatus;
import com.ismaelebonaventura.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
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
}

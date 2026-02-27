package com.ismaelebonaventura.auth_service.bootstrap;

import com.ismaelebonaventura.auth_service.config.AppProperties;
import com.ismaelebonaventura.auth_service.model.Role;
import com.ismaelebonaventura.auth_service.model.User;
import com.ismaelebonaventura.auth_service.model.UserStatus;
import com.ismaelebonaventura.auth_service.repository.UserRepository;
import com.ismaelebonaventura.auth_service.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final AppProperties properties;

    @Override
    public void run(ApplicationArguments args) {

        if (!properties.getBootstrap().getAdmin().isEnabled()) {
            return;
        }

        String email = properties.getBootstrap().getAdmin().getEmail();

        if (userRepository.existsByEmail(email)) {
            return;
        }

        String rawPassword = properties.getBootstrap().getAdmin().getPassword();
        String hash = passwordService.hash(rawPassword);

        User admin = new User(
                email,
                hash,
                Role.ADMIN,
                UserStatus.ACTIVE,
                null,
                null,
                null
        );

        userRepository.save(admin);
    }
}

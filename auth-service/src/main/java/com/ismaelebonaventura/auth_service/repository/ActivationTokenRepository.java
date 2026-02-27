package com.ismaelebonaventura.auth_service.repository;

import com.ismaelebonaventura.auth_service.model.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, UUID> {

    Optional<ActivationToken> findByToken(String token);
}

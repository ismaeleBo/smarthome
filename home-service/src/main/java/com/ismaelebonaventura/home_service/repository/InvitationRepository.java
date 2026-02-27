package com.ismaelebonaventura.home_service.repository;

import com.ismaelebonaventura.home_service.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    Optional<Invitation> findByToken(String token);

    boolean existsByHomeIdAndEmailAndConsumedFalseAndExpiresAtAfter(
            Integer homeId, String email, LocalDateTime now
    );
}
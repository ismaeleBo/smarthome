package com.ismaelebonaventura.auth_service.repository;

import com.ismaelebonaventura.auth_service.model.InvitationAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface InvitationAuthorizationRepository extends JpaRepository<InvitationAuthorization, String> {

    @Modifying
    @Query("""
                update InvitationAuthorization a
                set a.status = 'CONSUMED'
                where a.tokenHash = :tokenHash and a.status = 'VALID' and a.expiresAt > :now
            """)
    int consumeIfValid(String tokenHash, LocalDateTime now);
}
package com.ismaelebonaventura.home_service.repository;

import com.ismaelebonaventura.home_service.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    Optional<Invitation> findByToken(String token);

    List<Invitation> findByHomeId(Integer homeId);

    boolean existsByHomeIdAndEmailAndConsumedFalseAndRevokedFalseAndExpiresAtAfter(
            Integer homeId, String email, LocalDateTime now
    );

    @Modifying
    @Query("""
        update Invitation i
           set i.consumed = true,
               i.consumedAt = :now
         where i.homeId = :homeId
           and lower(i.email) = lower(:email)
           and i.consumed = false
           and i.revoked = false
           and i.expiresAt > :now
    """)
    int consumeActiveInvitation(@Param("homeId") Integer homeId,
                                @Param("email") String email,
                                @Param("now") LocalDateTime now);
}
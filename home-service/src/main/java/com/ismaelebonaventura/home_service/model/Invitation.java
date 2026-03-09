package com.ismaelebonaventura.home_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "invitations", indexes = {
        @Index(name = "idx_invitations_token", columnList = "token", unique = true),
        @Index(name = "idx_invitations_home_email", columnList = "home_id,email")
})
public class Invitation {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 128)
    private String token;

    @Column(name = "home_id", nullable = false)
    private Integer homeId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean consumed = false;

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

    @Getter
    @Column(nullable = false)
    private boolean revoked = false;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Invitation(String token, Integer homeId, String email, LocalDateTime expiresAt) {
        this.id = UUID.randomUUID();
        this.token = token;
        this.homeId = homeId;
        this.email = email.trim().toLowerCase();
        this.expiresAt = expiresAt;
        this.consumed = false;
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiresAt);
    }

    public void consume(LocalDateTime now) {
        if (this.revoked) {
            throw new IllegalStateException("Invitation revoked");
        }
        if (this.consumed) {
            throw new IllegalStateException("Invitation already consumed");
        }
        if (isExpired(now)) {
            throw new IllegalStateException("Invitation expired");
        }
        this.consumed = true;
        this.consumedAt = now;
    }

    public void revoke(LocalDateTime now) {
        if (this.consumed) {
            throw new IllegalStateException("Invitation already consumed");
        }
        if (this.revoked) {
            return;
        }
        this.revoked = true;
        this.revokedAt = now;
    }
}
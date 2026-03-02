package com.ismaelebonaventura.auth_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitation_authorizations")
@Getter
@Setter
@NoArgsConstructor
public class InvitationAuthorization {

    @Id
    @Column(name = "token_hash", length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private String email;

    @Column(name = "home_id", nullable = false)
    private Integer homeId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Status status;

    public enum Status {VALID, REVOKED, CONSUMED}
}
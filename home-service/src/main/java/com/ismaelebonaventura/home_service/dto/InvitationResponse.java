package com.ismaelebonaventura.home_service.dto;

import java.time.LocalDateTime;

import com.ismaelebonaventura.home_service.model.InvitationStatus;

public record InvitationResponse(
    String token,
    String email,
    LocalDateTime expiresAt,
    InvitationStatus status
) {}

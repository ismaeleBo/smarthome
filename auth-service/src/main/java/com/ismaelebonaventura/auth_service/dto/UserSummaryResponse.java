package com.ismaelebonaventura.auth_service.dto;

import com.ismaelebonaventura.auth_service.model.Role;
import com.ismaelebonaventura.auth_service.model.UserStatus;

import java.time.LocalDate;
import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Role role,
        UserStatus status
) {
}
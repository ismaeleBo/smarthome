package com.ismaelebonaventura.auth_service.dto;

import com.ismaelebonaventura.auth_service.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ProvisionUserRequest(
        @Email
        @NotBlank
        String email,
        @NotNull
        Role role,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotNull
        LocalDate birthDate
) {
}
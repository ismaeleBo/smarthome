package com.ismaelebonaventura.home_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInvitationRequest(
        @NotNull Integer homeId,
        @Email @NotBlank String email
) {}
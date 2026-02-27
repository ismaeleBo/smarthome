package com.ismaelebonaventura.auth_service.dto;

import java.time.LocalDate;

public record ActivateMemberRequest(
        String token,
        String password,
        String firstName,
        String lastName,
        LocalDate dateOfBirth
) {}

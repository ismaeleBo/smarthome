package com.ismaelebonaventura.auth_service.dto;

import java.util.UUID;

public record ProvisionUserResponse(
        UUID userId
) {
}
package com.ismaelebonaventura.auth_service.dto;

public record TokenResponse(
        String accessToken,
        String tokenType
) {
}
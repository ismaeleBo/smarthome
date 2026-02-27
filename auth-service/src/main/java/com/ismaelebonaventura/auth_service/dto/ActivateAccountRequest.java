package com.ismaelebonaventura.auth_service.dto;


public record ActivateAccountRequest(
        String token,
        String password
) {
}


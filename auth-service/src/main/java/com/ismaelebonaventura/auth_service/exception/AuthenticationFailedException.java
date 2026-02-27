package com.ismaelebonaventura.auth_service.exception;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException() {
        super("Invalid credentials or inactive account");
    }
}

package com.ismaelebonaventura.measurements_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of(HttpStatus.BAD_REQUEST.value(), "Validation error"));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
        return ResponseEntity.status(status)
                .body(ApiError.of(status, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenOperationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiError.of(403, ex.getMessage()));
    }

    @ExceptionHandler(IncompatibleCoverageException.class)
    public ResponseEntity<ApiError> handleIncompatibleCoverage(IncompatibleCoverageException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiError.of(409, ex.getMessage()));
    }

    public record ApiError(int status, String message, Instant timestamp) {
        public static ApiError of(int status, String message) {
            return new ApiError(status, message, Instant.now());
        }
    }
}
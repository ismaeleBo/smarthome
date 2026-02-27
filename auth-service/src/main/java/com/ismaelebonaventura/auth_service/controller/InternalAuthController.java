package com.ismaelebonaventura.auth_service.controller;

import com.ismaelebonaventura.auth_service.dto.ProvisionUserRequest;
import com.ismaelebonaventura.auth_service.dto.ProvisionUserResponse;
import com.ismaelebonaventura.auth_service.service.ProvisioningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalAuthController {

    private final ProvisioningService provisioningService;

    @PostMapping("/users")
    public ResponseEntity<ProvisionUserResponse> provisionUser(@Valid @RequestBody ProvisionUserRequest request) {

        var result = provisioningService.provisionInactiveUser(
                request.email(),
                request.role(),
                request.firstName(),
                request.lastName(),
                request.birthDate()
        );

        return ResponseEntity.ok(new ProvisionUserResponse(result.userId(), result.activationToken()));
    }
}
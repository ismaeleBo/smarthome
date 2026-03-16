package com.ismaelebonaventura.auth_service.controller;

import com.ismaelebonaventura.auth_service.dto.ProvisionUserRequest;
import com.ismaelebonaventura.auth_service.dto.ProvisionUserResponse;
import com.ismaelebonaventura.auth_service.dto.UserSummaryResponse;
import com.ismaelebonaventura.auth_service.model.Role;
import com.ismaelebonaventura.auth_service.service.AdminUsersService;
import com.ismaelebonaventura.auth_service.service.ProvisioningService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUsersController {

    private final AdminUsersService adminUsersService;
    private final ProvisioningService provisioningService;

    @GetMapping
    public List<UserSummaryResponse> getAll() {
        return adminUsersService.getAllUsers();
    }

    @GetMapping("/heads")
    public List<UserSummaryResponse> getHeads() {
        return adminUsersService.getUsersByRole(Role.HEAD);
    }

    @GetMapping(params = "role")
    public List<UserSummaryResponse> getByRole(@RequestParam Role role) {
        return adminUsersService.getUsersByRole(role);
    }

    @PostMapping("/provision")
    public ResponseEntity<ProvisionUserResponse> provisionUser(@Valid @RequestBody ProvisionUserRequest request) {

        var result = provisioningService.provisionInactiveUser(
                request.email(),
                request.role(),
                request.firstName(),
                request.lastName(),
                request.birthDate());

        return ResponseEntity.ok(new ProvisionUserResponse(result.userId()));
    }
}
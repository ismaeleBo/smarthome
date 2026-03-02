package com.ismaelebonaventura.auth_service.controller;

import com.ismaelebonaventura.auth_service.dto.*;
import com.ismaelebonaventura.auth_service.service.ActivationService;
import com.ismaelebonaventura.auth_service.service.AuthService;
import com.ismaelebonaventura.auth_service.service.MemberInvitationAcceptanceService;
import com.ismaelebonaventura.auth_service.service.MemberRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ActivationService activationService;
    private final MemberRegistrationService memberRegistrationService;
    private final MemberInvitationAcceptanceService memberInvitationAcceptanceService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {

        String token = authService.login(request.email(), request.password());

        return ResponseEntity.ok(
                new TokenResponse(token, "Bearer")
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        UUID userId = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        authService.changePassword(
                userId,
                request.currentPassword(),
                request.newPassword()
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/activate-account")
    public ResponseEntity<Void> activateAccount(@RequestBody ActivateAccountRequest request) {
        activationService.activateAccount(request.token(), request.password());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register-member")
    public ResponseEntity<Void> registerMember(@Valid @RequestBody RegisterMemberRequest request) {
        memberRegistrationService.registerMember(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> accept(@RequestBody AcceptInvitationRequest req) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        memberInvitationAcceptanceService.acceptInvitation(userId, req.token());
        return ResponseEntity.noContent().build();
    }

    public record AcceptInvitationRequest(String token) {
    }
}

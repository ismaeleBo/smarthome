package com.ismaelebonaventura.home_service.controller;

import com.ismaelebonaventura.home_service.dto.InvitationStatusResponse;
import com.ismaelebonaventura.home_service.model.InvitationStatus;
import com.ismaelebonaventura.home_service.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @GetMapping("/{token}")
    public InvitationStatusResponse getStatus(@PathVariable String token) {
        InvitationStatus status = invitationService.getInvitationStatus(token);
        return new InvitationStatusResponse(status);
    }

    @PostMapping("/{token}/consume")
    public ResponseEntity<Void> consume(@PathVariable String token) {

        UUID memberUserId = (UUID) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        invitationService.consumeInvitationAsMember(token, memberUserId);

        return ResponseEntity.ok().build();
    }
}
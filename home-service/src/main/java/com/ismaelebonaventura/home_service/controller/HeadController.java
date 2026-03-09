package com.ismaelebonaventura.home_service.controller;

import com.ismaelebonaventura.home_service.dto.CreateInvitationRequest;
import com.ismaelebonaventura.home_service.dto.CreateInvitationResponse;
import com.ismaelebonaventura.home_service.dto.InvitationResponse;
import com.ismaelebonaventura.home_service.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/head")
@RequiredArgsConstructor
public class HeadController {

    private final InvitationService invitationService;

    @GetMapping("/invitations/{homeId}")
    public ResponseEntity<List<InvitationResponse>> getInvitationsForHome(@PathVariable Integer homeId) {
        UUID headUserId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(invitationService.getInvitationsForHome(homeId, headUserId));
    }

    @PostMapping("/invitations")
    public ResponseEntity<CreateInvitationResponse> createInvitation(
            @RequestBody CreateInvitationRequest request
    ) {
        UUID headUserId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LocalDateTime expiresAt = invitationService.createMemberInvitation(
                headUserId,
                request.homeId(),
                request.email()
        );

        return ResponseEntity.ok(new CreateInvitationResponse(expiresAt));
    }

    @PostMapping("/invitations/{token}/revoke")
    public ResponseEntity<Void> revoke(@PathVariable String token) {
        UUID headUserId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        invitationService.revokeInvitation(headUserId, token);
        return ResponseEntity.noContent().build();
    }
}
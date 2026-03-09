package com.ismaelebonaventura.home_service.controller;

import com.ismaelebonaventura.home_service.dto.InvitationStatusResponse;
import com.ismaelebonaventura.home_service.model.InvitationStatus;
import com.ismaelebonaventura.home_service.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
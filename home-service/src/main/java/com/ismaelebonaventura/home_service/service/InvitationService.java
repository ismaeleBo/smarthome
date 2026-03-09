package com.ismaelebonaventura.home_service.service;

import com.ismaelebonaventura.home_service.aop.Audited;
import com.ismaelebonaventura.home_service.dto.InvitationResponse;
import com.ismaelebonaventura.home_service.exception.ConflictException;
import com.ismaelebonaventura.home_service.exception.ForbiddenOperationException;
import com.ismaelebonaventura.home_service.messaging.InvitationEventPublisher;
import com.ismaelebonaventura.home_service.model.*;
import com.ismaelebonaventura.home_service.repository.HomeRepository;
import com.ismaelebonaventura.home_service.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final HomeRepository homeRepository;
    private final InvitationRepository invitationRepository;
    private final InvitationEventPublisher eventPublisher;

    private final SecureRandom secureRandom = new SecureRandom();

    @Audited("CREATE_MEMBER_INVITATION")
    @Transactional
    public LocalDateTime createMemberInvitation(UUID headUserId, Integer homeId, String email) {

        if (headUserId == null)
            throw new IllegalArgumentException("headUserId is required");
        if (homeId == null)
            throw new IllegalArgumentException("homeId is required");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("email is required");

        String normalizedEmail = email.trim().toLowerCase();

        Home home = homeRepository.findByHomeId(homeId)
                .orElseThrow(() -> new IllegalArgumentException("Home not found"));

        if (home.getHeadUserId() == null || !home.getHeadUserId().equals(headUserId)) {
            throw new ForbiddenOperationException("Head is not assigned to this Home");
        }

        if (home.getStatus() != HomeStatus.CONFIGURED) {
            throw new IllegalStateException("Home must be CONFIGURED to create invitations");
        }

        LocalDateTime now = LocalDateTime.now();

        boolean alreadyActive = invitationRepository
                .existsByHomeIdAndEmailAndConsumedFalseAndRevokedFalseAndExpiresAtAfter(
                        homeId, normalizedEmail, now);

        if (alreadyActive) {
            throw new ConflictException("An active invitation already exists for this email");
        }

        String token = generateToken();
        LocalDateTime expiresAt = now.plusDays(7);

        Invitation invitation = new Invitation(token, homeId, normalizedEmail, expiresAt);
        invitationRepository.save(invitation);

        eventPublisher.publishInvitationCreated(token, normalizedEmail, homeId, expiresAt);

        return expiresAt;
    }

    @Transactional(readOnly = true)
    public InvitationStatus getInvitationStatus(String token) {

        if (token == null || token.isBlank())
            return InvitationStatus.NOT_FOUND;

        return invitationRepository.findByToken(token.trim())
                .map(this::geInvitationStatus)
                .orElse(InvitationStatus.NOT_FOUND);
    }

    @Audited("REVOKE_MEMBER_INVITATION")
    @Transactional
    public void revokeInvitation(UUID headUserId, String token) {

        if (headUserId == null)
            throw new IllegalArgumentException("headUserId is required");
        if (token == null || token.isBlank())
            throw new IllegalArgumentException("token is required");

        Invitation invitation = invitationRepository.findByToken(token.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

        Home home = homeRepository.findByHomeId(invitation.getHomeId())
                .orElseThrow(() -> new IllegalStateException("Home not found"));

        if (home.getHeadUserId() == null || !home.getHeadUserId().equals(headUserId)) {
            throw new ForbiddenOperationException("Head is not assigned to this Home");
        }

        LocalDateTime now = LocalDateTime.now();
        invitation.revoke(now);

        eventPublisher.publishInvitationRevoked(invitation.getToken());
    }

    public List<InvitationResponse> getInvitationsForHome(Integer homeId, UUID headUserId) {
        if (homeId == null)
            throw new IllegalArgumentException("homeId is required");
        if (headUserId == null)
            throw new IllegalArgumentException("headUserId is required");

        Home home = homeRepository.findByHomeId(homeId)
                .orElseThrow(() -> new IllegalStateException("Home not found"));

        if (home.getHeadUserId() == null || !home.getHeadUserId().equals(headUserId)) {
            throw new ForbiddenOperationException("Head is not assigned to this Home");
        }

        return invitationRepository.findByHomeId(homeId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private InvitationStatus geInvitationStatus(Invitation invitation) {
        if (invitation == null)
            return InvitationStatus.NOT_FOUND;
        LocalDateTime now = LocalDateTime.now();
        if (invitation.isRevoked())
            return InvitationStatus.REVOKED;
        if (invitation.isExpired(now))
            return InvitationStatus.EXPIRED;
        if (invitation.isConsumed())
            return InvitationStatus.CONSUMED;
        return InvitationStatus.VALID;
    }

    private InvitationResponse toResponse(Invitation invitation) {
        return new InvitationResponse(
                invitation.getToken(),
                invitation.getEmail(),
                invitation.getExpiresAt(),
                geInvitationStatus(invitation));
    }
}
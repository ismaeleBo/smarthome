package com.ismaelebonaventura.home_service.service;

import com.ismaelebonaventura.home_service.aop.Audited;
import com.ismaelebonaventura.home_service.exception.ConflictException;
import com.ismaelebonaventura.home_service.exception.ForbiddenOperationException;
import com.ismaelebonaventura.home_service.exception.NotFoundException;
import com.ismaelebonaventura.home_service.messaging.NotificationPublisher;
import com.ismaelebonaventura.home_service.model.*;
import com.ismaelebonaventura.home_service.repository.HomeMemberRepository;
import com.ismaelebonaventura.home_service.repository.HomeRepository;
import com.ismaelebonaventura.home_service.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final HomeRepository homeRepository;
    private final InvitationRepository invitationRepository;
    private final HomeMemberRepository homeMemberRepository;
    private final NotificationPublisher notificationPublisher;

    private final SecureRandom secureRandom = new SecureRandom();

    @Audited("CREATE_MEMBER_INVITATION")
    @Transactional
    public LocalDateTime createMemberInvitation(UUID headUserId, Integer homeId, String email) {

        if (headUserId == null) {
            throw new IllegalArgumentException("headUserId is required");
        }
        if (homeId == null) {
            throw new IllegalArgumentException("homeId is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        String normalizedEmail = email.trim().toLowerCase();

        Home home = homeRepository.findByHomeId(homeId)
                .orElseThrow(() -> new NotFoundException("Home not found"));

        if (home.getHeadUserId() == null || !home.getHeadUserId().equals(headUserId)) {
            throw new ForbiddenOperationException("Head is not assigned to this Home");
        }

        if (home.getStatus() != HomeStatus.CONFIGURED) {
            throw new ConflictException("Home must be CONFIGURED to create invitations");
        }

        LocalDateTime now = LocalDateTime.now();

        boolean alreadyActive = invitationRepository
                .existsByHomeIdAndEmailAndConsumedFalseAndExpiresAtAfter(homeId, normalizedEmail, now);

        if (alreadyActive) {
            throw new ConflictException("An active invitation already exists for this email");
        }

        String token = generateToken();
        LocalDateTime expiresAt = now.plusDays(7);

        Invitation invitation = new Invitation(token, homeId, normalizedEmail, expiresAt);
        invitationRepository.save(invitation);

        notificationPublisher.publishMemberInvitationEmail(normalizedEmail, token, expiresAt);

        return expiresAt;
    }

    @Transactional(readOnly = true)
    public InvitationStatus getInvitationStatus(String token) {

        if (token == null || token.isBlank()) {
            return InvitationStatus.NOT_FOUND;
        }

        return invitationRepository.findByToken(token.trim())
                .map(inv -> {
                    LocalDateTime now = LocalDateTime.now();
                    if (inv.isExpired(now)) return InvitationStatus.EXPIRED;
                    if (inv.isConsumed()) return InvitationStatus.CONSUMED;
                    return InvitationStatus.VALID;
                })
                .orElse(InvitationStatus.NOT_FOUND);
    }

    @Audited("CONSUME_MEMBER_INVITATION")
    @Transactional
    public void consumeInvitationAsMember(String token, UUID memberUserId) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token is required");
        }
        if (memberUserId == null) {
            throw new IllegalArgumentException("Member userId is required");
        }

        Invitation invitation = invitationRepository.findByToken(token.trim())
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

        LocalDateTime now = LocalDateTime.now();

        if (invitation.isExpired(now)) {
            throw new ConflictException("Invitation expired");
        }
        if (invitation.isConsumed()) {
            throw new ConflictException("Invitation already consumed");
        }

        if (homeMemberRepository.existsByHomeIdAndMemberUserId(invitation.getHomeId(), memberUserId)) {
            throw new ConflictException("Member already associated to this Home");
        }

        Home home = homeRepository.findByHomeId(invitation.getHomeId())
                .orElseThrow(() -> new NotFoundException("Home not found"));

        if (home.getStatus() == HomeStatus.DISABLED) {
            throw new ConflictException("Home is disabled");
        }

        invitation.consume(now);
        homeMemberRepository.save(new HomeMember(invitation.getHomeId(), memberUserId));
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
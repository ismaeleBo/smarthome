package com.ismaelebonaventura.home_service.service;

import com.ismaelebonaventura.home_service.model.Home;
import com.ismaelebonaventura.home_service.model.HomeMember;
import com.ismaelebonaventura.home_service.model.HomeStatus;
import com.ismaelebonaventura.home_service.repository.HomeMemberRepository;
import com.ismaelebonaventura.home_service.repository.HomeRepository;
import com.ismaelebonaventura.home_service.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberAssociationService {
    private final HomeRepository homeRepository;
    private final HomeMemberRepository homeMemberRepository;
    private final InvitationRepository invitationRepository;

    @Transactional
    public void associateMemberToHome(UUID memberUserId, Integer homeId, String email) {

        if (memberUserId == null) throw new IllegalArgumentException("memberUserId is required");
        if (homeId == null) throw new IllegalArgumentException("homeId is required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email is required");

        String normalizedEmail = email.trim().toLowerCase();

        Home home = homeRepository.findByHomeId(homeId)
                .orElseThrow(() -> new IllegalStateException("Home not found: " + homeId));

        if (home.getStatus() == HomeStatus.DISABLED) {
            throw new IllegalStateException("Home is disabled: " + homeId);
        }

        if (!homeMemberRepository.existsByHomeIdAndMemberUserId(homeId, memberUserId)) {
            homeMemberRepository.save(new HomeMember(homeId, memberUserId));
        }

        var now = LocalDateTime.now();
        int updated = invitationRepository.consumeActiveInvitation(homeId, normalizedEmail, now);

        if (updated == 0) {
            log.debug("No active invitation to consume for homeId={} email={}", homeId, normalizedEmail);
        }
    }
}
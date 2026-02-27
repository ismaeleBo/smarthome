package com.ismaelebonaventura.home_service.service;

import com.ismaelebonaventura.home_service.dto.UserAssignmentResponse;
import com.ismaelebonaventura.home_service.repository.AnalystHomeRepository;
import com.ismaelebonaventura.home_service.repository.HomeMemberRepository;
import com.ismaelebonaventura.home_service.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAssignmentsService {

    private final HomeRepository homeRepository;
    private final HomeMemberRepository homeMemberRepository;
    private final AnalystHomeRepository analystHomeRepository;

    @Transactional(readOnly = true)
    public UserAssignmentResponse getAssignment(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        // HEAD
        var headHomeIds = homeRepository.findHomeIdsByHeadUserId(userId);
        if (!headHomeIds.isEmpty()) {
            return UserAssignmentResponse.head(userId, headHomeIds);
        }

        // MEMBER
        var memberHomeIds = homeMemberRepository.findHomeIdsByMemberUserId(userId);
        if (!memberHomeIds.isEmpty()) {
            return UserAssignmentResponse.member(userId, memberHomeIds);
        }

        // ANALYST
        var analystHomeIds = analystHomeRepository.findHomeIdsByAnalystUserId(userId);
        if (!analystHomeIds.isEmpty()) {
            return UserAssignmentResponse.analyst(userId, analystHomeIds);
        }

        return UserAssignmentResponse.none(userId);
    }
}
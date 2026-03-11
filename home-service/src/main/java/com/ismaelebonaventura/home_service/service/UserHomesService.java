package com.ismaelebonaventura.home_service.service;

import com.ismaelebonaventura.home_service.dto.UserHomeInfoResponse;
import com.ismaelebonaventura.home_service.dto.UserHomesResponse;
import com.ismaelebonaventura.home_service.repository.AnalystHomeRepository;
import com.ismaelebonaventura.home_service.repository.HomeMemberRepository;
import com.ismaelebonaventura.home_service.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserHomesService {

    private final HomeRepository homeRepository;
    private final HomeMemberRepository homeMemberRepository;
    private final AnalystHomeRepository analystHomeRepository;

    @Transactional(readOnly = true)
    public UserHomesResponse getUserHomes(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        Set<Integer> homeIds = new HashSet<>();
        homeIds.addAll(homeRepository.findHomeIdsByHeadUserId(userId));
        homeIds.addAll(homeMemberRepository.findHomeIdsByMemberUserId(userId));
        homeIds.addAll(analystHomeRepository.findHomeIdsByAnalystUserId(userId));

        if (homeIds.isEmpty()) {
            return new UserHomesResponse(userId, List.of());
        }

        List<UserHomeInfoResponse> homes = homeRepository.findByHomeIdIn(homeIds)
                .stream()
                .map(home -> new UserHomeInfoResponse(
                        home.getHomeId(),
                        home.getStatus(),
                        home.getPricePerKwh()))
                .sorted(Comparator.comparing(UserHomeInfoResponse::homeId))
                .toList();

        return new UserHomesResponse(userId, homes);
    }
}
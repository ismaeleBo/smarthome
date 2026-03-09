package com.ismaelebonaventura.home_service.service;

import com.ismaelebonaventura.home_service.aop.Audited;
import com.ismaelebonaventura.home_service.model.AnalystHome;
import com.ismaelebonaventura.home_service.repository.AnalystHomeRepository;
import com.ismaelebonaventura.home_service.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalystAccessService {

    private final AnalystHomeRepository analystHomeRepository;
    private final HomeRepository homeRepository;

    @Audited("ASSIGN_ANALYST_HOMES")
    @Transactional
    public void assignHomes(UUID analystUserId, List<Integer> homeIds) {

        if (analystUserId == null) {
            throw new IllegalArgumentException("analystUserId is required");
        }
        if (homeIds == null) {
            throw new IllegalArgumentException("homeIds is required");
        }

        var newHomeIds = new HashSet<>(homeIds);

        for (Integer homeId : newHomeIds) {
            if (homeId == null) {
                throw new IllegalArgumentException("homeId cannot be null");
            }
            boolean exists = homeRepository.existsByHomeId(homeId);
            if (!exists) {
                throw new IllegalArgumentException("Home not found: " + homeId);
            }
        }

        var currentAssignments = analystHomeRepository.findByAnalystUserId(analystUserId);
        var currentHomeIds = currentAssignments.stream()
                .map(AnalystHome::getHomeId)
                .collect(java.util.stream.Collectors.toSet());

        var homeIdsToAdd = new HashSet<>(newHomeIds);
        homeIdsToAdd.removeAll(currentHomeIds);

        var homeIdsToRemove = new HashSet<>(currentHomeIds);
        homeIdsToRemove.removeAll(newHomeIds);

        for (Integer homeId : homeIdsToAdd) {
            analystHomeRepository.save(new AnalystHome(analystUserId, homeId));
        }

        if (!homeIdsToRemove.isEmpty()) {
            analystHomeRepository.deleteByAnalystUserIdAndHomeIdIn(analystUserId, homeIdsToRemove);
        }
    }

    public List<Integer> getAssignedHomeIds(UUID analystUserId) {
        if (analystUserId == null) {
            throw new IllegalArgumentException("analystUserId is required");
        }
        return analystHomeRepository.findHomeIdsByAnalystUserId(analystUserId);
    }
}
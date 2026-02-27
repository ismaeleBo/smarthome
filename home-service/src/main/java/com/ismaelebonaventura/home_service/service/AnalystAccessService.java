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
        if (homeIds == null || homeIds.isEmpty()) {
            throw new IllegalArgumentException("homeIds is required");
        }

        var uniqueHomeIds = new HashSet<>(homeIds);

        for (Integer homeId : uniqueHomeIds) {
            if (homeId == null) {
                throw new IllegalArgumentException("homeId cannot be null");
            }
            boolean exists = homeRepository.existsByHomeId(homeId);
            if (!exists) {
                throw new IllegalArgumentException("Home not found: " + homeId);
            }
        }

        for (Integer homeId : uniqueHomeIds) {
            if (!analystHomeRepository.existsByAnalystUserIdAndHomeId(analystUserId, homeId)) {
                analystHomeRepository.save(new AnalystHome(analystUserId, homeId));
            }
        }
    }
}
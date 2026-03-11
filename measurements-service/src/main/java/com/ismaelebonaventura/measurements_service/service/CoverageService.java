package com.ismaelebonaventura.measurements_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ismaelebonaventura.measurements_service.client.HomeAuthorizationClient;
import com.ismaelebonaventura.measurements_service.dto.CoverageResponse;
import com.ismaelebonaventura.measurements_service.exception.ForbiddenOperationException;
import com.ismaelebonaventura.measurements_service.repository.MeasurementRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoverageService {

    private final MeasurementRepository repository;
    private final HomeAuthorizationClient homeAuthorizationClient;

    @Transactional(readOnly = true)
    public CoverageResponse getCoverage(Integer homeId, UUID userId, String role) {

        if (homeId == null) {
            throw new IllegalArgumentException("homeId is required");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        boolean isAdmin = "ADMIN".equals(role);

        if (!isAdmin && !homeAuthorizationClient.canAccessHome(userId, homeId)) {
            throw new ForbiddenOperationException("User is not authorized for this home");
        }

        return new CoverageResponse(
                repository.findMinTimeByHomeId(homeId),
                repository.findMaxTimeByHomeId(homeId));
    }
}

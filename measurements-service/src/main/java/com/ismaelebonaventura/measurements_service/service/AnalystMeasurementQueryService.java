package com.ismaelebonaventura.measurements_service.service;

import com.ismaelebonaventura.measurements_service.client.HomeAuthorizationClient;
import com.ismaelebonaventura.measurements_service.dto.CoverageResponse;
import com.ismaelebonaventura.measurements_service.dto.DeviceResponse;
import com.ismaelebonaventura.measurements_service.exception.ForbiddenOperationException;
import com.ismaelebonaventura.measurements_service.exception.IncompatibleCoverageException;
import com.ismaelebonaventura.measurements_service.repository.MeasurementRepository;
import com.ismaelebonaventura.measurements_service.security.Roles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AnalystMeasurementQueryService {

    private final MeasurementRepository measurementRepository;
    private final HomeAuthorizationClient homeAuthorizationClient;

    @Transactional(readOnly = true)
    public CoverageResponse getCompatibleCoverage(List<Integer> homeIds, UUID userId, String role) {

        validateAnalystRequest(homeIds, userId, role);
        validateAccessibleHomes(homeIds, userId);

        List<LocalDateTime> minTimes = homeIds.stream()
                .map(measurementRepository::findMinTimeByHomeId)
                .filter(Objects::nonNull)
                .toList();

        List<LocalDateTime> maxTimes = homeIds.stream()
                .map(measurementRepository::findMaxTimeByHomeId)
                .filter(Objects::nonNull)
                .toList();

        if (minTimes.size() != homeIds.size() || maxTimes.size() != homeIds.size()) {
            throw new IllegalStateException("Coverage not available for one or more selected homes");
        }

        LocalDateTime compatibleFrom = minTimes.stream()
                .max(LocalDateTime::compareTo)
                .orElseThrow(() -> new IllegalStateException("Cannot compute compatible coverage"));

        LocalDateTime compatibleTo = maxTimes.stream()
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new IllegalStateException("Cannot compute compatible coverage"));

        if (compatibleFrom.isAfter(compatibleTo)) {
            throw new IncompatibleCoverageException("Selected homes do not share a compatible time range");
        }

        return new CoverageResponse(compatibleFrom, compatibleTo);
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> getCommonDevices(List<Integer> homeIds, UUID userId, String role) {

        validateAnalystRequest(homeIds, userId, role);
        validateAccessibleHomes(homeIds, userId);

        List<Set<String>> deviceSets = homeIds.stream()
                .<Set<String>>map(id -> new HashSet<>(
                        measurementRepository.findDistinctApplianceTypesByHomeId(id)))
                .toList();

        if (deviceSets.isEmpty()) {
            return List.of();
        }

        Set<String> intersection = new HashSet<>(deviceSets.getFirst());

        for (int i = 1; i < deviceSets.size(); i++) {
            intersection.retainAll(deviceSets.get(i));
        }

        return intersection.stream()
                .sorted()
                .map(DeviceResponse::new)
                .toList();
    }

    private void validateAnalystRequest(List<Integer> homeIds, UUID userId, String role) {
        System.out.println("Validating analyst request: userId=" + userId + ", role=" + role + ", homeIds=" + homeIds);
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        if (role == null || !role.equals(Roles.ANALYST)) {
            throw new ForbiddenOperationException("Only analysts can access these endpoints");
        }
        if (homeIds == null || homeIds.isEmpty()) {
            throw new IllegalArgumentException("homeIds must not be empty");
        }
        if (homeIds.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("homeIds must not contain null values");
        }
    }

    private void validateAccessibleHomes(List<Integer> homeIds, UUID userId) {
        Set<Integer> accessibleHomeIds = homeAuthorizationClient.getAccessibleHomeIds(userId);

        boolean allAccessible = homeIds.stream()
                .allMatch(accessibleHomeIds::contains);

        if (!allAccessible) {
            throw new ForbiddenOperationException("One or more selected homes are not accessible to the analyst");
        }
    }
}

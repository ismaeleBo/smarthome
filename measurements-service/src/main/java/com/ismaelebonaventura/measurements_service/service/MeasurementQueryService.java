package com.ismaelebonaventura.measurements_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ismaelebonaventura.measurements_service.client.HomeAuthorizationClient;
import com.ismaelebonaventura.measurements_service.dto.DeviceResponse;
import com.ismaelebonaventura.measurements_service.dto.MeasurementResponse;
import com.ismaelebonaventura.measurements_service.exception.ForbiddenOperationException;
import com.ismaelebonaventura.measurements_service.model.Measurement;
import com.ismaelebonaventura.measurements_service.repository.MeasurementRepository;
import com.ismaelebonaventura.measurements_service.security.Roles;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeasurementQueryService {
    private final MeasurementRepository repository;
    private final HomeAuthorizationClient homeAuthorizationClient;

    @Transactional(readOnly = true)
    public List<DeviceResponse> getDevices(Integer homeId) {
        if (homeId == null) {
            throw new IllegalArgumentException("homeId is required");
        }

        return repository.findDistinctApplianceTypesByHomeId(homeId)
                .stream()
                .map(DeviceResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> getDevices(Integer homeId, UUID userId, String role) {

        if (homeId == null) {
            throw new IllegalArgumentException("homeId is required");
        }
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        boolean isAdmin = Roles.ADMIN.equals(role);

        if (!isAdmin && !homeAuthorizationClient.canAccessHome(userId, homeId)) {
            throw new ForbiddenOperationException("User is not authorized for this home");
        }

        return getDevices(homeId);
    }

    @Transactional(readOnly = true)
    public List<MeasurementResponse> getMeasurements(
            Integer homeId,
            LocalDateTime from,
            LocalDateTime to,
            String applianceType) {
        if (homeId == null) {
            throw new IllegalArgumentException("homeId is required");
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("from and to are required");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must be before or equal to to");
        }

        List<Measurement> measurements;

        if (applianceType == null || applianceType.isBlank()) {
            measurements = repository.findByHomeIdAndMeasurementTimeBetweenOrderByMeasurementTimeAsc(
                    homeId, from, to);
        } else {
            measurements = repository.findByHomeIdAndMeasurementTimeBetweenAndApplianceTypeOrderByMeasurementTimeAsc(
                    homeId, from, to, applianceType.trim());
        }

        return measurements.stream()
                .map(this::toResponse)
                .toList();
    }

    private MeasurementResponse toResponse(Measurement m) {
        return new MeasurementResponse(
                m.getMeasurementTime(),
                m.getApplianceType(),
                m.getEnergyConsumptionKwh(),
                m.getOutdoorTemperatureC());
    }
}
package com.ismaelebonaventura.measurements_service.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ismaelebonaventura.measurements_service.dto.DeviceResponse;
import com.ismaelebonaventura.measurements_service.dto.MeasurementResponse;
import com.ismaelebonaventura.measurements_service.service.MeasurementQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/homes")
@RequiredArgsConstructor
public class InternalMeasurementController {

    private final MeasurementQueryService measurementQueryService;

    @GetMapping("/{homeId}/devices")
    public List<DeviceResponse> getDevices(@PathVariable Integer homeId) {
        return measurementQueryService.getDevices(homeId);
    }

    @GetMapping("/{homeId}/measurements")
    public List<MeasurementResponse> getMeasurements(
            @PathVariable Integer homeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String applianceType) {
        return measurementQueryService.getMeasurements(homeId, from, to, applianceType);
    }
}

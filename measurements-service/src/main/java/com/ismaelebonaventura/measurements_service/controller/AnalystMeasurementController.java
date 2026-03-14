package com.ismaelebonaventura.measurements_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ismaelebonaventura.measurements_service.dto.AnalystHomesRequest;
import com.ismaelebonaventura.measurements_service.dto.CoverageResponse;
import com.ismaelebonaventura.measurements_service.dto.DeviceResponse;
import com.ismaelebonaventura.measurements_service.service.AnalystMeasurementQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/analyst/homes")
@RequiredArgsConstructor
public class AnalystMeasurementController {

    private final AnalystMeasurementQueryService analystMeasurementQueryService;

    @PostMapping("/coverage")
    public CoverageResponse getCompatibleCoverage(@RequestBody AnalystHomesRequest request) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String role = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("Role not found"));

        return analystMeasurementQueryService.getCompatibleCoverage(
                request.homeIds(),
                userId,
                role);
    }

    @PostMapping("/devices")
    public List<DeviceResponse> getCommonDevices(@RequestBody AnalystHomesRequest request) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String role = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("Role not found"));

        return analystMeasurementQueryService.getCommonDevices(
                request.homeIds(),
                userId,
                role);
    }
}

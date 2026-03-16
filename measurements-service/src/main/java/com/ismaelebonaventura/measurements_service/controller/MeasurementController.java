package com.ismaelebonaventura.measurements_service.controller;

import com.ismaelebonaventura.measurements_service.dto.CoverageResponse;
import com.ismaelebonaventura.measurements_service.dto.DeviceResponse;
import com.ismaelebonaventura.measurements_service.service.CoverageService;
import com.ismaelebonaventura.measurements_service.service.MeasurementQueryService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementQueryService measurementQueryService;
    private final CoverageService coverageService;

    @GetMapping("/homes/{homeId}/coverage")
    public CoverageResponse getCoverage(@PathVariable Integer homeId) {
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String role = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("Role not found"));

        return coverageService.getCoverage(homeId, userId, role);
    }

    @GetMapping("/homes/{homeId}/devices")
    public List<DeviceResponse> getDevices(@PathVariable Integer homeId) {
        UUID userId = (UUID) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        String role = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("Role not found"));

        return measurementQueryService.getDevices(homeId, userId, role);
    }
}
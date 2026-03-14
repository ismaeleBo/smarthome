package com.ismaelebonaventura.analytics_service.dto;

public record TopDeviceDto(
        String applianceType,
        double totalConsumptionKwh,
        double estimatedCost,
        String prevalentTimeBand) {
}

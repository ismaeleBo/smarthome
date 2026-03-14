package com.ismaelebonaventura.analytics_service.dto;

public record AverageByDeviceDto(
        String applianceType,
        double averageConsumptionKwh,
        double estimatedCost) {
}

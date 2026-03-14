package com.ismaelebonaventura.analytics_service.dto;

public record AverageByPeriodDto(
        String label,
        double averageConsumptionKwh,
        double estimatedCost) {
}

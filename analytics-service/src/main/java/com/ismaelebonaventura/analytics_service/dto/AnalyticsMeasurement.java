package com.ismaelebonaventura.analytics_service.dto;

import java.time.LocalDateTime;

public record AnalyticsMeasurement(
        Integer homeId,
        String applianceType,
        LocalDateTime measurementTime,
        double energyConsumptionKwh,
        Double outdoorTemperatureC,
        double pricePerKwh) {
}

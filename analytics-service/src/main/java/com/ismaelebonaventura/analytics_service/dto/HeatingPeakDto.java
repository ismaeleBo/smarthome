package com.ismaelebonaventura.analytics_service.dto;

import java.time.LocalDateTime;

public record HeatingPeakDto(
        LocalDateTime time,
        double consumptionKwh,
        Double outdoorTemperatureC) {
}

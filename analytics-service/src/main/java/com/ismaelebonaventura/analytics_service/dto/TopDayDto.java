package com.ismaelebonaventura.analytics_service.dto;

import java.time.LocalDate;

public record TopDayDto(
        LocalDate day,
        double totalConsumptionKwh,
        double estimatedCost) {
}

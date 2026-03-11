package com.ismaelebonaventura.measurements_service.dto;

import java.time.LocalDateTime;

public record MeasurementResponse(
		LocalDateTime measurementTime,
		String applianceType,
		double energyConsumptionKwh,
		Double outdoorTemperatureC) {
}

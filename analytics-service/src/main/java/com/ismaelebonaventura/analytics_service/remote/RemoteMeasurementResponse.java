package com.ismaelebonaventura.analytics_service.remote;

import java.time.LocalDateTime;

public record RemoteMeasurementResponse(
		Integer homeId,
		LocalDateTime measurementTime,
		String applianceType,
		double energyConsumptionKwh,
		Double outdoorTemperatureC) {
}
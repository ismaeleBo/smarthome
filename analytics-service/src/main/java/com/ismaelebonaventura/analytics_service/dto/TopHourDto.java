package com.ismaelebonaventura.analytics_service.dto;

public record TopHourDto(
		int hour,
		double totalConsumptionKwh,
		double estimatedCost) {
}

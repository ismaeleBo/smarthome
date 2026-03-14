package com.ismaelebonaventura.analytics_service.analyzer;

import com.ismaelebonaventura.analytics_service.aggregation.AnalyticsAggregationContext;
import com.ismaelebonaventura.analytics_service.dto.TopHourDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TopHoursAnalyzer {

	public List<TopHourDto> analyze(AnalyticsAggregationContext context) {
		return context.byHour().entrySet().stream()
				.map(entry -> new TopHourDto(
						entry.getKey(),
						entry.getValue().getTotalConsumptionKwh(),
						entry.getValue().getTotalCost()))
				.sorted(Comparator.comparing(TopHourDto::totalConsumptionKwh).reversed())
				.toList();
	}
}
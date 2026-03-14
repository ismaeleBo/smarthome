package com.ismaelebonaventura.analytics_service.analyzer;

import com.ismaelebonaventura.analytics_service.aggregation.AnalyticsAggregationContext;
import com.ismaelebonaventura.analytics_service.dto.AverageByPeriodDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AverageByWeekdayAnalyzer {

    public List<AverageByPeriodDto> analyze(AnalyticsAggregationContext context) {
        return context.byWeekday().entrySet().stream()
                .map(entry -> new AverageByPeriodDto(
                        entry.getKey().name(),
                        entry.getValue().getAverageConsumptionKwh(),
                        entry.getValue().getAverageCost()))
                .toList();
    }
}
package com.ismaelebonaventura.analytics_service.analyzer;

import com.ismaelebonaventura.analytics_service.aggregation.AnalyticsAggregationContext;
import com.ismaelebonaventura.analytics_service.dto.AverageByPeriodDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AverageByHourAnalyzer {

    public List<AverageByPeriodDto> analyze(AnalyticsAggregationContext context) {
        return context.byHour().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new AverageByPeriodDto(
                        String.valueOf(entry.getKey()),
                        entry.getValue().getAverageConsumptionKwh(),
                        entry.getValue().getAverageCost()))
                .toList();
    }
}
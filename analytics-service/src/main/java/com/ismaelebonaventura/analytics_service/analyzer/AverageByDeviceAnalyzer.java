package com.ismaelebonaventura.analytics_service.analyzer;

import com.ismaelebonaventura.analytics_service.aggregation.AnalyticsAggregationContext;
import com.ismaelebonaventura.analytics_service.dto.AverageByDeviceDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AverageByDeviceAnalyzer {

    public List<AverageByDeviceDto> analyze(AnalyticsAggregationContext context) {
        return context.byDevice().entrySet().stream()
                .map(entry -> new AverageByDeviceDto(
                        entry.getKey(),
                        entry.getValue().getAverageConsumptionKwh(),
                        entry.getValue().getAverageCost()))
                .toList();
    }
}
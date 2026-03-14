package com.ismaelebonaventura.analytics_service.analyzer;

import com.ismaelebonaventura.analytics_service.aggregation.AnalyticsAggregationContext;
import com.ismaelebonaventura.analytics_service.dto.TopDayDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TopDaysAnalyzer {

    public List<TopDayDto> analyze(AnalyticsAggregationContext context) {
        return context.byDay().entrySet().stream()
                .map(entry -> new TopDayDto(
                        entry.getKey(),
                        entry.getValue().getTotalConsumptionKwh(),
                        entry.getValue().getTotalCost()))
                .sorted(Comparator.comparing(TopDayDto::totalConsumptionKwh).reversed())
                .toList();
    }
}
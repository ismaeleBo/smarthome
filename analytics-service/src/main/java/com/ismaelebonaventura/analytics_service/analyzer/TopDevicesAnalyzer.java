package com.ismaelebonaventura.analytics_service.analyzer;

import com.ismaelebonaventura.analytics_service.aggregation.AnalyticsAggregationContext;
import com.ismaelebonaventura.analytics_service.dto.TopDeviceDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TopDevicesAnalyzer {

    public List<TopDeviceDto> analyze(AnalyticsAggregationContext context) {
        return context.byDevice().entrySet().stream()
                .map(entry -> new TopDeviceDto(
                        entry.getKey(),
                        entry.getValue().getTotalConsumptionKwh(),
                        entry.getValue().getTotalCost(),
                        entry.getValue().getPrevalentTimeBand()))
                .sorted(Comparator.comparing(TopDeviceDto::totalConsumptionKwh).reversed())
                .toList();
    }
}
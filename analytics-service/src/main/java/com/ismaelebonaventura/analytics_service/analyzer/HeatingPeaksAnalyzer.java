package com.ismaelebonaventura.analytics_service.analyzer;

import com.ismaelebonaventura.analytics_service.aggregation.AnalyticsAggregationContext;
import com.ismaelebonaventura.analytics_service.dto.HeatingPeakDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class HeatingPeaksAnalyzer {

    public List<HeatingPeakDto> analyze(AnalyticsAggregationContext context) {
        return context.heatingMeasurements().stream()
                .sorted(Comparator.comparingDouble(m -> -m.energyConsumptionKwh()))
                .limit(10)
                .map(m -> new HeatingPeakDto(
                        m.measurementTime(),
                        m.energyConsumptionKwh(),
                        m.outdoorTemperatureC()))
                .toList();
    }
}
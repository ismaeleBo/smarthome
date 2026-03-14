package com.ismaelebonaventura.analytics_service.aggregation;

import java.util.HashMap;
import java.util.Map;

public class DeviceAggregationBucket extends AggregationBucket {

    private final Map<String, Double> consumptionByTimeBand = new HashMap<>();

    public void addTimeBandConsumption(String timeBand, double consumptionKwh) {
        consumptionByTimeBand.merge(timeBand, consumptionKwh, Double::sum);
    }

    public String getPrevalentTimeBand() {
        return consumptionByTimeBand.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("UNKNOWN");
    }
}
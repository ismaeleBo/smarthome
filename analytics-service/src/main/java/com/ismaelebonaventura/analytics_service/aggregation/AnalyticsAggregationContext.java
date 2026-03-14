package com.ismaelebonaventura.analytics_service.aggregation;

import com.ismaelebonaventura.analytics_service.dto.AnalyticsMeasurement;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

public record AnalyticsAggregationContext(
        List<AnalyticsMeasurement> measurements,
        Map<Integer, AggregationBucket> byHour,
        Map<LocalDate, AggregationBucket> byDay,
        Map<String, DeviceAggregationBucket> byDevice,
        Map<DayOfWeek, AggregationBucket> byWeekday,
        Map<Month, AggregationBucket> byMonth,
        List<AnalyticsMeasurement> heatingMeasurements) {
}
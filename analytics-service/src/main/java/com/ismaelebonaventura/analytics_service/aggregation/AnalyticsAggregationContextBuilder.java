package com.ismaelebonaventura.analytics_service.aggregation;

import com.ismaelebonaventura.analytics_service.dto.AnalyticsMeasurement;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Component
public class AnalyticsAggregationContextBuilder {

    private static final String HEATING_TYPE = "Heater";

    public AnalyticsAggregationContext build(List<AnalyticsMeasurement> measurements) {

        Map<Integer, AggregationBucket> byHour = new HashMap<>();
        Map<LocalDate, AggregationBucket> byDay = new HashMap<>();
        Map<String, DeviceAggregationBucket> byDevice = new HashMap<>();
        Map<DayOfWeek, AggregationBucket> byWeekday = new HashMap<>();
        Map<Month, AggregationBucket> byMonth = new HashMap<>();
        List<AnalyticsMeasurement> heatingMeasurements = new ArrayList<>();

        for (AnalyticsMeasurement m : measurements) {
            double consumption = m.energyConsumptionKwh();
            double price = m.pricePerKwh();

            byHour.computeIfAbsent(m.measurementTime().getHour(), h -> new AggregationBucket())
                    .add(consumption, price);

            byDay.computeIfAbsent(m.measurementTime().toLocalDate(), d -> new AggregationBucket())
                    .add(consumption, price);

            byWeekday.computeIfAbsent(m.measurementTime().getDayOfWeek(), d -> new AggregationBucket())
                    .add(consumption, price);

            byMonth.computeIfAbsent(m.measurementTime().getMonth(), d -> new AggregationBucket())
                    .add(consumption, price);

            DeviceAggregationBucket deviceBucket = byDevice.computeIfAbsent(
                    m.applianceType(),
                    d -> new DeviceAggregationBucket());
            deviceBucket.add(consumption, price);
            deviceBucket.addTimeBandConsumption(toTimeBand(m.measurementTime().getHour()), consumption);

            if (HEATING_TYPE.equalsIgnoreCase(m.applianceType())) {
                heatingMeasurements.add(m);
            }
        }

        return new AnalyticsAggregationContext(
                measurements,
                byHour,
                byDay,
                byDevice,
                byWeekday,
                byMonth,
                heatingMeasurements);
    }

    private String toTimeBand(int hour) {
        if (hour <= 5)
            return "NIGHT";
        if (hour <= 11)
            return "MORNING";
        if (hour <= 17)
            return "AFTERNOON";
        return "EVENING";
    }
}
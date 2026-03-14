package com.ismaelebonaventura.analytics_service.service;

import com.ismaelebonaventura.analytics_service.aggregation.AnalyticsAggregationContext;
import com.ismaelebonaventura.analytics_service.aggregation.AnalyticsAggregationContextBuilder;
import com.ismaelebonaventura.analytics_service.analyzer.*;
import com.ismaelebonaventura.analytics_service.aspect.TimedAnalytics;
import com.ismaelebonaventura.analytics_service.client.HomeInternalClient;
import com.ismaelebonaventura.analytics_service.client.MeasurementInternalClient;
import com.ismaelebonaventura.analytics_service.dto.*;
import com.ismaelebonaventura.analytics_service.exception.ForbiddenOperationException;
import com.ismaelebonaventura.analytics_service.remote.RemoteMeasurementResponse;
import com.ismaelebonaventura.analytics_service.remote.RemoteUserHomeInfoResponse;
import com.ismaelebonaventura.analytics_service.remote.RemoteUserHomesResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class HomeDashboardService {

	private final HomeInternalClient homeInternalClient;
	private final MeasurementInternalClient measurementInternalClient;
	private final AnalyticsAggregationContextBuilder aggregationContextBuilder;

	private final TopHoursAnalyzer topHoursAnalyzer;
	private final TopDaysAnalyzer topDaysAnalyzer;
	private final TopDevicesAnalyzer topDevicesAnalyzer;
	private final HeatingPeaksAnalyzer heatingPeaksAnalyzer;
	private final AverageByDeviceAnalyzer averageByDeviceAnalyzer;
	private final AverageByHourAnalyzer averageByHourAnalyzer;
	private final AverageByWeekdayAnalyzer averageByWeekdayAnalyzer;
	private final AverageByMonthAnalyzer averageByMonthAnalyzer;

	@Qualifier("analyticsExecutor")
	private final Executor analyticsExecutor;

	@TimedAnalytics
	public HomeDashboardResponse getDashboard(
			UUID userId,
			String role,
			Integer homeId,
			LocalDateTime from,
			LocalDateTime to,
			String applianceType) {
		validateInput(userId, homeId, from, to);

		RemoteUserHomesResponse userHomes = homeInternalClient.getUserHomes(userId);

		RemoteUserHomeInfoResponse homeInfo = userHomes.homes().stream()
				.filter(h -> homeId.equals(h.homeId()))
				.findFirst()
				.orElseThrow(() -> new ForbiddenOperationException(
						"User is not authorized for this home"));

		if (!"CONFIGURED".equals(homeInfo.status())) {
			throw new IllegalStateException("Home must be CONFIGURED");
		}

		List<RemoteMeasurementResponse> rawMeasurements = measurementInternalClient.getMeasurements(homeId,
				from, to,
				applianceType);

		List<AnalyticsMeasurement> measurements = rawMeasurements.stream()
				.map(m -> new AnalyticsMeasurement(
						homeId,
						m.applianceType(),
						m.measurementTime(),
						m.energyConsumptionKwh(),
						m.outdoorTemperatureC(),
						homeInfo.pricePerKwh()))
				.toList();

		AnalyticsAggregationContext context = aggregationContextBuilder.build(measurements);

		CompletableFuture<List<TopHourDto>> topHoursFuture = CompletableFuture
				.supplyAsync(() -> topHoursAnalyzer.analyze(context), analyticsExecutor);

		CompletableFuture<List<TopDayDto>> topDaysFuture = CompletableFuture
				.supplyAsync(() -> topDaysAnalyzer.analyze(context), analyticsExecutor);

		CompletableFuture<List<TopDeviceDto>> topDevicesFuture = CompletableFuture
				.supplyAsync(() -> topDevicesAnalyzer.analyze(context), analyticsExecutor);

		CompletableFuture<List<HeatingPeakDto>> heatingPeaksFuture = CompletableFuture
				.supplyAsync(() -> heatingPeaksAnalyzer.analyze(context), analyticsExecutor);

		CompletableFuture<List<AverageByDeviceDto>> averageByDeviceFuture = CompletableFuture
				.supplyAsync(() -> averageByDeviceAnalyzer.analyze(context), analyticsExecutor);

		CompletableFuture<List<AverageByPeriodDto>> averageByHourFuture = CompletableFuture
				.supplyAsync(() -> averageByHourAnalyzer.analyze(context), analyticsExecutor);

		CompletableFuture<List<AverageByPeriodDto>> averageByWeekdayFuture = CompletableFuture
				.supplyAsync(() -> averageByWeekdayAnalyzer.analyze(context), analyticsExecutor);

		CompletableFuture<List<AverageByPeriodDto>> averageByMonthFuture = CompletableFuture
				.supplyAsync(() -> averageByMonthAnalyzer.analyze(context), analyticsExecutor);

		return new HomeDashboardResponse(
				homeId,
				applianceType,
				from,
				to,
				topHoursFuture.join(),
				topDaysFuture.join(),
				topDevicesFuture.join(),
				heatingPeaksFuture.join(),
				averageByDeviceFuture.join(),
				averageByHourFuture.join(),
				averageByWeekdayFuture.join(),
				averageByMonthFuture.join());
	}

	private void validateInput(UUID userId, Integer homeId, LocalDateTime from, LocalDateTime to) {
		if (userId == null) {
			throw new IllegalArgumentException("userId is required");
		}
		if (homeId == null) {
			throw new IllegalArgumentException("homeId is required");
		}
		if (from == null || to == null) {
			throw new IllegalArgumentException("from and to are required");
		}
		if (from.isAfter(to)) {
			throw new IllegalArgumentException("from must be before or equal to to");
		}
	}
}
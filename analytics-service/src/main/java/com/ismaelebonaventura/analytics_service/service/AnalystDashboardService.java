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
import com.ismaelebonaventura.analytics_service.security.Roles;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalystDashboardService {

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
	public AnalystDashboardResponse getDashboard(
			UUID userId,
			String role,
			List<Integer> selectedHomeIds,
			LocalDateTime from,
			LocalDateTime to,
			String applianceType) {
		validateInput(userId, role, selectedHomeIds, from, to);

		RemoteUserHomesResponse userHomes = homeInternalClient.getUserHomes(userId);

		Set<Integer> accessibleHomeIds = userHomes.homes().stream()
				.map(RemoteUserHomeInfoResponse::homeId)
				.collect(Collectors.toSet());

		boolean allAccessible = selectedHomeIds.stream().allMatch(accessibleHomeIds::contains);
		if (!allAccessible) {
			throw new ForbiddenOperationException("One or more selected homes are not accessible to the analyst");
		}

		List<RemoteUserHomeInfoResponse> selectedConfiguredHomes = userHomes.homes().stream()
				.filter(h -> selectedHomeIds.contains(h.homeId()))
				.filter(h -> "CONFIGURED".equals(h.status()))
				.toList();

		if (selectedConfiguredHomes.isEmpty()) {
			throw new IllegalStateException("No configured homes available in the selected subset");
		}

		List<CompletableFuture<List<AnalyticsMeasurement>>> measurementFutures = selectedConfiguredHomes.stream()
				.map(home -> CompletableFuture.supplyAsync(() -> {
					List<RemoteMeasurementResponse> rawMeasurements = measurementInternalClient.getMeasurements(
							home.homeId(),
							from,
							to,
							applianceType);

					return rawMeasurements.stream()
							.map(m -> new AnalyticsMeasurement(
									home.homeId(),
									m.applianceType(),
									m.measurementTime(),
									m.energyConsumptionKwh(),
									m.outdoorTemperatureC(),
									home.pricePerKwh()))
							.toList();
				}, analyticsExecutor))
				.toList();

		List<AnalyticsMeasurement> allMeasurements = measurementFutures.stream()
				.map(CompletableFuture::join)
				.flatMap(List::stream)
				.toList();

		AnalyticsAggregationContext context = aggregationContextBuilder.build(allMeasurements);

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

		return new AnalystDashboardResponse(
				selectedConfiguredHomes.stream().map(RemoteUserHomeInfoResponse::homeId).toList(),
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

	private void validateInput(
			UUID userId,
			String role,
			List<Integer> selectedHomeIds,
			LocalDateTime from,
			LocalDateTime to) {
		if (userId == null) {
			throw new IllegalArgumentException("userId is required");
		}
		if (!Roles.ANALYST.equals(role)) {
			throw new ForbiddenOperationException("Only analysts can access aggregated dashboard");
		}
		if (selectedHomeIds == null || selectedHomeIds.isEmpty()) {
			throw new IllegalArgumentException("homeIds must not be empty");
		}
		if (selectedHomeIds.stream().anyMatch(id -> id == null)) {
			throw new IllegalArgumentException("homeIds must not contain null values");
		}
		if (from == null || to == null) {
			throw new IllegalArgumentException("from and to are required");
		}
		if (from.isAfter(to)) {
			throw new IllegalArgumentException("from must be before or equal to to");
		}
	}
}
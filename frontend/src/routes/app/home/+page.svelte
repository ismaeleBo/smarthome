<script lang="ts">
	import { onMount } from "svelte";
	import { session } from "$lib/stores/session.svelte";
	import { HomeApi } from "$lib/api/home";
	import { MeasurementsApi } from "$lib/api/measurements";
	import { AnalyticsApi } from "$lib/api/analytics";
	import { HttpError } from "$lib/api/http";

	import type { HomeResponse } from "$lib/contracts/home";
	import type { ApplianceTypeResponse, CoverageResponse } from "$lib/contracts/measurements";
	import type {
		AverageByPeriodDto,
		AverageByDeviceDto,
		HomeDashboardResponse
	} from "$lib/contracts/analytics";
	import type { DateTimeRange } from "$lib/utils/datetime";

	import {
		clampRangeToCoverage,
		fromCoverageToRange,
		isRangeInsideCoverage,
	} from "$lib/utils/datetime";

	import SingleHomeSelector from "$lib/components/home/SingleHomeSelector.svelte";
	import HomeDetailsCard from "$lib/components/home/HomeDetailsCard.svelte";
	import DateTimeRangeFilter from "$lib/components/filters/DateTimeRangeFilter.svelte";
	import DeviceSelect from "$lib/components/filters/DeviceSelect.svelte";

	import AnalyticsSummaryCards from "$lib/components/analytics/AnalyticsSummaryCards.svelte";
	import ChartTypeToggle from "$lib/components/analytics/ChartTypeToggle.svelte";
	import DualMetricChart from "$lib/components/analytics/DualMetricChart.svelte";
	import TopDevicesTable from "$lib/components/analytics/TopDevicesTable.svelte";
	import HeatingPeaksTable from "$lib/components/analytics/HeatingPeaksTable.svelte";

	const s = session.state;

	let pageLoading = $state(true);
	let pageError: string | null = $state(null);

	let detailsLoading = $state(false);
	let detailsError: string | null = $state(null);
	let selectedHomeDetails: HomeResponse | null = $state(null);

	let filtersLoading = $state(false);
	let filtersError: string | null = $state(null);

	let coverage: CoverageResponse | null = $state(null);
	let range: DateTimeRange | null = $state(null);

	let devices: ApplianceTypeResponse[] = $state([]);
	let selectedDevice: string | null = $state(null);

	let dashboardLoading = $state(false);
	let dashboardError: string | null = $state(null);
	let dashboard: HomeDashboardResponse | null = $state(null);

	let topHoursMode: "bar" | "line" = $state("bar");
	let topDaysMode: "bar" | "line" = $state("bar");
	let avgByHourMode: "bar" | "line" = $state("line");
	let avgByWeekdayMode: "bar" | "line" = $state("line");
	let avgByMonthMode: "bar" | "line" = $state("line");
	let avgByDeviceMode: "bar" | "line" = $state("bar");

	function parseError(error: unknown, fallback = "Operation failed"): string {
		if (error instanceof HttpError) return error.apiError?.message ?? error.message;
		if (error instanceof Error) return error.message;
		return fallback;
	}

	async function loadSelectedHomeDetails() {
		detailsError = null;
		selectedHomeDetails = null;

		if (!s.token || s.selectedHomeId == null) return;

		detailsLoading = true;
		try {
			selectedHomeDetails = await HomeApi.getHome(s.token, s.selectedHomeId);
		} catch (error) {
			detailsError = parseError(error, "Unable to load home details");
		} finally {
			detailsLoading = false;
		}
	}

	function ensureSelectedDeviceIsValid() {
		if (!selectedDevice) return;
		const stillAvailable = devices.some((d) => d.applianceType === selectedDevice);
		if (!stillAvailable) selectedDevice = null;
	}

	function applyCoverage(nextCoverage: CoverageResponse) {
		coverage = nextCoverage;

		if (!range) {
			range = fromCoverageToRange(nextCoverage);
			return;
		}

		if (isRangeInsideCoverage(range, nextCoverage)) return;
		range = clampRangeToCoverage(range, nextCoverage);
	}

	async function loadHomeFilters() {
		filtersError = null;
		coverage = null;
		range = null;
		devices = [];
		selectedDevice = null;

		if (!s.token || s.selectedHomeId == null) return;

		filtersLoading = true;
		try {
			const [coverageRes, devicesRes] = await Promise.all([
				MeasurementsApi.homeCoverage(s.token, s.selectedHomeId),
				MeasurementsApi.homeDevices(s.token, s.selectedHomeId)
			]);

			applyCoverage(coverageRes);
			devices = devicesRes;
			ensureSelectedDeviceIsValid();
		} catch (error) {
			filtersError = parseError(error, "Unable to load analytics filters");
		} finally {
			filtersLoading = false;
		}
	}

	async function loadDashboard() {
		dashboardError = null;
		dashboard = null;

		if (!s.token || s.selectedHomeId == null || !range) return;

		dashboardLoading = true;
		try {
			dashboard = await AnalyticsApi.homeDashboard(s.token, s.selectedHomeId, {
				from: `${range.from}:00`,
				to: `${range.to}:00`,
				applianceType: selectedDevice
			});
		} catch (error) {
			dashboardError = parseError(error, "Unable to load dashboard");
		} finally {
			dashboardLoading = false;
		}
	}

	async function reloadPageContext() {
		pageError = null;

		try {
			await loadSelectedHomeDetails();
			await loadHomeFilters();
			await loadDashboard();
		} catch (error) {
			pageError = parseError(error, "Unable to load page data");
		}
	}

	async function handleHomeChange(homeId: number) {
		session.selectHome(homeId);
		await reloadPageContext();
	}

	async function handleRangeChange(nextRange: DateTimeRange) {
		if (!coverage) return;
		range = clampRangeToCoverage(nextRange, coverage);
		await loadDashboard();
	}

	async function handleDeviceChange(device: string | null) {
		selectedDevice = device;
		await loadDashboard();
	}

	function chartDataFromPeriod(rows: AverageByPeriodDto[]) {
		return {
			labels: rows.map((r) => r.label),
			consumption: rows.map((r) => r.averageConsumptionKwh),
			costs: rows.map((r) => r.estimatedCost)
		};
	}

	function chartDataFromAverageByDevice(rows: AverageByDeviceDto[]) {
		return {
			labels: rows.map((r) => r.applianceType),
			consumption: rows.map((r) => r.averageConsumptionKwh),
			costs: rows.map((r) => r.estimatedCost)
		};
	}

	function summaryCards() {
		if (!dashboard) return [];

		const topHour = dashboard.topHours[0];
		const topDay = dashboard.topDays[0];
		const topDevice = dashboard.topDevices[0];

		return [
			{
				label: "Top hour",
				value: topHour ? `${topHour.hour}:00` : "-",
				subtitle: topHour
					? `${topHour.totalConsumptionKwh.toFixed(2)} kWh - ${topHour.estimatedCost.toFixed(2)}€`
					: undefined
			},
			{
				label: "Top device",
				value: topDevice ? topDevice.applianceType : "-",
				subtitle: topDevice
					? `${topDevice.totalConsumptionKwh.toFixed(2)} kWh - ${topDevice.estimatedCost.toFixed(2)}€`
					: undefined
			},
			{
				label: "Top day",
				value: topDay ? topDay.day : "-",
				subtitle: topDay
					? `${topDay.totalConsumptionKwh.toFixed(2)} kWh - ${topDay.estimatedCost.toFixed(2)}€`
					: undefined
			}
		];
	}

	onMount(async () => {
		pageLoading = true;
		await reloadPageContext();
		pageLoading = false;
	});

	$effect(() => {
		if (s.selectedHomeId != null) {
			void reloadPageContext();
		}
	});
</script>

<div class="space-y-6 p-6">
	{#if pageError}
		<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">{pageError}</div>
	{/if}

	<HomeDetailsCard loading={detailsLoading} error={detailsError} home={selectedHomeDetails} />

	<section>
		{#if filtersError}
			<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">{filtersError}</div>
		{/if}

		{#if pageLoading || filtersLoading}
			<p class="text-sm opacity-70">Loading filters...</p>
		{:else if coverage && range}
			<div class="grid gap-4 xl:grid-cols-[1.4fr,0.8fr]">
				<DateTimeRangeFilter
					range={range}
					min={fromCoverageToRange(coverage).from}
					max={fromCoverageToRange(coverage).to}
					onChange={handleRangeChange}
				/>

				<DeviceSelect devices={devices} selectedDevice={selectedDevice} onChange={handleDeviceChange} />
			</div>
		{/if}
	</section>

	<section class="space-y-4">
		<div class="flex items-center justify-between">
			{#if dashboardLoading}
				<div class="text-sm opacity-70">Loading dashboard...</div>
			{/if}
		</div>

		{#if dashboardError}
			<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">{dashboardError}</div>
		{:else if dashboard}
			<AnalyticsSummaryCards cards={summaryCards()} />

			<div class="grid gap-6 xl:grid-cols-2">
				<div class="space-y-2">
					<div class="flex items-center justify-between">
						<h3 class="text-lg font-semibold">Top hours</h3>
						<ChartTypeToggle value={topHoursMode} onChange={(v) => (topHoursMode = v)} />
					</div>

					<DualMetricChart
						title="Top hours"
						labels={dashboard.topHours.map((x) => `${x.hour}:00`)}
						consumption={dashboard.topHours.map((x) => x.totalConsumptionKwh)}
						costs={dashboard.topHours.map((x) => x.estimatedCost)}
						mode={topHoursMode}
					/>
				</div>

				<div class="space-y-2">
					<div class="flex items-center justify-between">
						<h3 class="text-lg font-semibold">Top days</h3>
						<ChartTypeToggle value={topDaysMode} onChange={(v) => (topDaysMode = v)} />
					</div>

					<DualMetricChart
						title="Top days"
						labels={dashboard.topDays.map((x) => x.day)}
						consumption={dashboard.topDays.map((x) => x.totalConsumptionKwh)}
						costs={dashboard.topDays.map((x) => x.estimatedCost)}
						mode={topDaysMode}
						height={360}
					/>
				</div>

				<TopDevicesTable rows={dashboard.topDevices} />
				{#if dashboard.heatingPeaks.length > 0}
					<HeatingPeaksTable rows={dashboard.heatingPeaks} />
				{/if}

				<div class="space-y-2">
					<div class="flex items-center justify-between">
						<h3 class="text-lg font-semibold">Average by hour</h3>
						<ChartTypeToggle value={avgByHourMode} onChange={(v) => (avgByHourMode = v)} />
					</div>

					{#key avgByHourMode}
						<DualMetricChart
							title="Average by hour"
							labels={chartDataFromPeriod(dashboard.averageByHour).labels}
							consumption={chartDataFromPeriod(dashboard.averageByHour).consumption}
							costs={chartDataFromPeriod(dashboard.averageByHour).costs}
							mode={avgByHourMode}
						/>
					{/key}
				</div>

				<div class="space-y-2">
					<div class="flex items-center justify-between">
						<h3 class="text-lg font-semibold">Average by weekday</h3>
						<ChartTypeToggle value={avgByWeekdayMode} onChange={(v) => (avgByWeekdayMode = v)} />
					</div>

					{#key avgByWeekdayMode}
						<DualMetricChart
							title="Average by weekday"
							labels={chartDataFromPeriod(dashboard.averageByWeekday).labels}
							consumption={chartDataFromPeriod(dashboard.averageByWeekday).consumption}
							costs={chartDataFromPeriod(dashboard.averageByWeekday).costs}
							mode={avgByWeekdayMode}
						/>
					{/key}
				</div>

				<div class="space-y-2">
					<div class="flex items-center justify-between">
						<h3 class="text-lg font-semibold">Average by month</h3>
						<ChartTypeToggle value={avgByMonthMode} onChange={(v) => (avgByMonthMode = v)} />
					</div>

					{#key avgByMonthMode}
						<DualMetricChart
							title="Average by month"
							labels={chartDataFromPeriod(dashboard.averageByMonth).labels}
							consumption={chartDataFromPeriod(dashboard.averageByMonth).consumption}
							costs={chartDataFromPeriod(dashboard.averageByMonth).costs}
							mode={avgByMonthMode}
						/>
					{/key}
				</div>

				<div class="space-y-2">
					<div class="flex items-center justify-between">
						<h3 class="text-lg font-semibold">Average by device</h3>
						<ChartTypeToggle value={avgByDeviceMode} onChange={(v) => (avgByDeviceMode = v)} />
					</div>

					{#key avgByDeviceMode}
						<DualMetricChart
							title="Average by device"
							labels={chartDataFromAverageByDevice(dashboard.averageByDevice).labels}
							consumption={chartDataFromAverageByDevice(dashboard.averageByDevice).consumption}
							costs={chartDataFromAverageByDevice(dashboard.averageByDevice).costs}
							mode={avgByDeviceMode}
						/>
					{/key}
				</div>
			</div>
		{:else}
			<div class="rounded-2xl border bg-white p-5 text-sm opacity-70">
				No dashboard data available.
			</div>
		{/if}
	</section>
</div>
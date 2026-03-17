<script lang="ts">
	import { onMount } from 'svelte';
	import { session } from '$lib/stores/session.svelte';
	import { MeasurementsApi } from '$lib/api/measurements';
	import { AnalyticsApi } from '$lib/api/analytics';
	import { HttpError } from '$lib/api/http';

	import type { ApplianceTypeResponse, CoverageResponse } from '$lib/contracts/measurements';
	import type {
		AnalystDashboardResponse,
		AverageByDeviceDto,
		AverageByPeriodDto
	} from '$lib/contracts/analytics';
	import type { DateTimeRange } from '$lib/utils/datetime';

	import {
		clampRangeToCoverage,
		fromCoverageToRange,
		isRangeInsideCoverage
	} from '$lib/utils/datetime';

	import MultiHomeSelector from '$lib/components/home/MultiHomeSelector.svelte';
	import DateTimeRangeFilter from '$lib/components/filters/DateTimeRangeFilter.svelte';
	import DeviceSelect from '$lib/components/filters/DeviceSelect.svelte';

	import AnalyticsSummaryCards from '$lib/components/analytics/AnalyticsSummaryCards.svelte';
	import ChartTypeToggle from '$lib/components/analytics/ChartTypeToggle.svelte';
	import DualMetricChart from '$lib/components/analytics/DualMetricChart.svelte';
	import TopDevicesTable from '$lib/components/analytics/TopDevicesTable.svelte';
	import HeatingPeaksTable from '$lib/components/analytics/HeatingPeaksTable.svelte';
	import {
		chartDataFromAverageByDevice,
		chartDataFromHour,
		chartDataFromPeriod,
		summaryCards
	} from '$lib/utils/analytics';

	const s = session.state;

	let selectedHomeIds: number[] = $state([]);

	let coverage: CoverageResponse | null = $state(null);
	let range: DateTimeRange | null = $state(null);

	let devices: ApplianceTypeResponse[] = $state([]);
	let selectedDevice: string | null = $state(null);

	let topHoursMode: 'bar' | 'line' = $state('bar');
	let topDaysMode: 'bar' | 'line' = $state('bar');
	let avgByHourMode: 'bar' | 'line' = $state('line');
	let avgByWeekdayMode: 'bar' | 'line' = $state('line');
	let avgByMonthMode: 'bar' | 'line' = $state('line');
	let avgByDeviceMode: 'bar' | 'line' = $state('bar');

	let filtersPromise = $state<Promise<{
		coverage: CoverageResponse;
		devices: ApplianceTypeResponse[];
	}> | null>(null);

	let dashboardPromise = $derived.by(() => {
		if (!s.token || selectedHomeIds.length === 0 || !range) return Promise.resolve(null);
		return AnalyticsApi.analystDashboard(s.token, {
			homeIds: selectedHomeIds,
			from: `${range.from}:00`,
			to: `${range.to}:00`,
			applianceType: selectedDevice ?? undefined
		});
	});

	function parseError(error: unknown, fallback = 'Operation failed'): string {
		if (error instanceof HttpError) return error.apiError?.message ?? error.message;
		if (error instanceof Error) return error.message;
		return fallback;
	}

	function availableHomeIds(): number[] {
		return s.homes.map((h) => h.homeId);
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

	function handleToggleHome(homeId: number, checked: boolean) {
		if (checked) {
			if (!selectedHomeIds.includes(homeId)) {
				selectedHomeIds = [...selectedHomeIds, homeId].sort((a, b) => a - b);
			}
		} else {
			selectedHomeIds = selectedHomeIds.filter((id) => id !== homeId);
		}
	}

	function handleSelectAll() {
		selectedHomeIds = availableHomeIds();
	}

	function handleDeselectAll() {
		selectedHomeIds = [];
	}

	function handleRangeChange(nextRange: DateTimeRange) {
		if (!coverage) return;
		range = clampRangeToCoverage(nextRange, coverage);
	}

	function handleDeviceChange(device: string | null) {
		selectedDevice = device;
	}

	onMount(() => {
		selectedHomeIds = availableHomeIds();
	});

	$effect(() => {
		if (selectedHomeIds.length > 0 && s.token) {
			range = null;
			filtersPromise = (async () => {
				const body = { homeIds: selectedHomeIds };
				const [coverageRes, devicesRes] = await Promise.all([
					MeasurementsApi.analystHomesCoverage(s.token!, body),
					MeasurementsApi.analystHomesDevices(s.token!, body)
				]);
				applyCoverage(coverageRes);
				devices = devicesRes;
				ensureSelectedDeviceIsValid();
				return { coverage: coverageRes, devices: devicesRes };
			})();
		} else {
			filtersPromise = null;
			coverage = null;
			range = null;
			devices = [];
		}
	});
</script>

<div class="space-y-6 p-6">
	<MultiHomeSelector
		homeIds={availableHomeIds()}
		{selectedHomeIds}
		onToggle={handleToggleHome}
		onSelectAll={handleSelectAll}
		onDeselectAll={handleDeselectAll}
	/>

	{#if selectedHomeIds.length === 0}
		<section class="rounded-2xl border bg-white p-5">
			<p class="text-sm opacity-70">Select at least one home to view analytics.</p>
		</section>
	{:else}
		<section>
			{#await filtersPromise}
				<p class="text-sm opacity-70">Loading filters...</p>
			{:then _}
				{#if coverage && range}
					<div class="grid gap-4 xl:grid-cols-[1.4fr,0.8fr]">
						<DateTimeRangeFilter
							{range}
							min={fromCoverageToRange(coverage).from}
							max={fromCoverageToRange(coverage).to}
							onChange={handleRangeChange}
						/>

						<DeviceSelect {devices} {selectedDevice} onChange={handleDeviceChange} />
					</div>
				{:else}
					<p class="text-sm opacity-70">No filters available for the selected homes.</p>
				{/if}
			{:catch error}
				<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
					{parseError(error, 'Unable to load analytics filters')}
				</div>
			{/await}
		</section>

		<section class="space-y-4">
			{#await dashboardPromise}
				<div class="text-sm opacity-70">Loading dashboard...</div>
			{:then dashboard}
				{#if dashboard}
					<AnalyticsSummaryCards cards={summaryCards(dashboard)} />

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
								labels={dashboard.topDays.map((x) => `${x.day}`)}
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
									labels={chartDataFromHour(dashboard.averageByHour).labels}
									consumption={chartDataFromHour(dashboard.averageByHour).consumption}
									costs={chartDataFromHour(dashboard.averageByHour).costs}
									mode={avgByHourMode}
								/>
							{/key}
						</div>

						<div class="space-y-2">
							<div class="flex items-center justify-between">
								<h3 class="text-lg font-semibold">Average by weekday</h3>
								<ChartTypeToggle
									value={avgByWeekdayMode}
									onChange={(v) => (avgByWeekdayMode = v)}
								/>
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
			{:catch error}
				<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
					{parseError(error, 'Unable to load dashboard')}
				</div>
			{/await}
		</section>
	{/if}
</div>

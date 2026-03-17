import type {
	AnalystDashboardResponse,
	AverageByDeviceDto,
	AverageByPeriodDto,
	HomeDashboardResponse
} from '$lib/contracts/analytics';

export function chartDataFromHour(rows: AverageByPeriodDto[]) {
	return {
		labels: rows.map((r) => `${r.label}:00`),
		consumption: rows.map((r) => r.averageConsumptionKwh),
		costs: rows.map((r) => r.estimatedCost)
	};
}

export function chartDataFromPeriod(rows: AverageByPeriodDto[]) {
	return {
		labels: rows.map((r) => r.label),
		consumption: rows.map((r) => r.averageConsumptionKwh),
		costs: rows.map((r) => r.estimatedCost)
	};
}

export function chartDataFromAverageByDevice(rows: AverageByDeviceDto[]) {
	return {
		labels: rows.map((r) => r.applianceType),
		consumption: rows.map((r) => r.averageConsumptionKwh),
		costs: rows.map((r) => r.estimatedCost)
	};
}

export function summaryCards(dashboard: HomeDashboardResponse | AnalystDashboardResponse | null) {
	if (!dashboard) return [];

	const topHour = dashboard.topHours[0];
	const topDay = dashboard.topDays[0];
	const topDevice = dashboard.topDevices[0];

	return [
		{
			label: 'Top hour',
			value: topHour ? `${topHour.hour}:00` : '-',
			subtitle: topHour
				? `${topHour.totalConsumptionKwh.toFixed(2)} kWh - ${topHour.estimatedCost.toFixed(2)}€`
				: undefined
		},
		{
			label: 'Top device',
			value: topDevice ? topDevice.applianceType : '-',
			subtitle: topDevice
				? `${topDevice.totalConsumptionKwh.toFixed(2)} kWh - ${topDevice.estimatedCost.toFixed(2)}€`
				: undefined
		},
		{
			label: 'Top day',
			value: topDay ? topDay.day : '-',
			subtitle: topDay
				? `${topDay.totalConsumptionKwh.toFixed(2)} kWh - ${topDay.estimatedCost.toFixed(2)}€`
				: undefined
		}
	];
}

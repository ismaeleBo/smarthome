export type TopHourDto = {
	hour: number;
	totalConsumptionKwh: number;
	estimatedCost: number;
};

export type TopDayDto = {
	day: string;
	totalConsumptionKwh: number;
	estimatedCost: number;
};

export type TopDeviceDto = {
	applianceType: string;
	totalConsumptionKwh: number;
	estimatedCost: number;
	prevalentTimeBand: string;
};

export type HeatingPeakDto = {
	time: string; // ISO date-time
	consumptionKwh: number;
	outdoorTemperatureC: number | null;
};

export type AverageByDeviceDto = {
	applianceType: string;
	averageConsumptionKwh: number;
	estimatedCost: number;
};

export type AverageByPeriodDto = {
	label: string;
	averageConsumptionKwh: number;
	estimatedCost: number;
};

export type HomeDashboardResponse = {
	homeId: number;
	applianceTypeFilter: string | null;
	from: string;
	to: string;
	topHours: TopHourDto[];
	topDays: TopDayDto[];
	topDevices: TopDeviceDto[];
	heatingPeaks: HeatingPeakDto[];
	averageByDevice: AverageByDeviceDto[];
	averageByHour: AverageByPeriodDto[];
	averageByWeekday: AverageByPeriodDto[];
	averageByMonth: AverageByPeriodDto[];
};

export type AnalystDashboardResponse = {
	homeIds: number[];
	applianceTypeFilter: string | null;
	from: string;
	to: string;
	topHours: TopHourDto[];
	topDays: TopDayDto[];
	topDevices: TopDeviceDto[];
	heatingPeaks: HeatingPeakDto[];
	averageByDevice: AverageByDeviceDto[];
	averageByHour: AverageByPeriodDto[];
	averageByWeekday: AverageByPeriodDto[];
	averageByMonth: AverageByPeriodDto[];
};

export type AnalystDashboardRequest = {
	homeIds: number[];
	from: string;
	to: string;
	applianceType?: string;
};

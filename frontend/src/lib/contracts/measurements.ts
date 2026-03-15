export type DatasetHomeIdsResponse = number[];

export type CoverageResponse = {
	from: string;
	to: string;
};

export type ApplianceTypeResponse = {
	applianceType: string;
};

export type ApplianceTypeListResponse = ApplianceTypeResponse[];

export type AnalystHomeIdsRequest = {
	homeIds: number[];
};

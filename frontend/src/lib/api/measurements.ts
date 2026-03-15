import { apiFetch } from './http';
import type {
	AnalystHomeIdsRequest,
	ApplianceTypeListResponse,
	CoverageResponse,
	DatasetHomeIdsResponse
} from '$lib/contracts/measurements';

export const MeasurementsApi = {
	homes: (token: string) => apiFetch<DatasetHomeIdsResponse>('/measurements/homes', { token }),

	importDataset: (token: string) =>
		apiFetch<void>('/measurements/admin/import', { method: 'POST', token }),

	homeCoverage: (token: string, homeId: number) =>
		apiFetch<CoverageResponse>(`/measurements/homes/${homeId}/coverage`, { token }),

	analystHomesCoverage: (token: string, body: AnalystHomeIdsRequest) =>
		apiFetch<CoverageResponse>('/measurements/analyst/homes/coverage', {
			method: 'POST',
			token,
			body
		}),

	homeDevices: (token: string, homeId: number) =>
		apiFetch<ApplianceTypeListResponse>(`/measurements/homes/${homeId}/devices`, { token }),

	analystHomesDevices: (token: string, body: AnalystHomeIdsRequest) =>
		apiFetch<ApplianceTypeListResponse>('/measurements/analyst/homes/devices', {
			method: 'POST',
			token,
			body
		})
};

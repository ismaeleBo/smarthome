import { apiFetch } from './http';
import type { DatasetHomeIdsResponse } from '$lib/contracts/measurements';

export const MeasurementsApi = {
	homes: (token: string) => apiFetch<DatasetHomeIdsResponse>('/measurements/homes', { token }),

	importDataset: (token: string) =>
		apiFetch<void>('/measurements/admin/import', { method: 'POST', token })
};

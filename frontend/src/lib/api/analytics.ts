import { apiFetch } from './http';
import type {
	AnalystDashboardRequest,
	AnalystDashboardResponse,
	HomeDashboardResponse
} from '$lib/contracts/analytics';

export const AnalyticsApi = {
	homeDashboard: (
		token: string,
		homeId: number,
		params: {
			from: string;
			to: string;
			applianceType?: string | null;
		}
	) => {
		const query = new URLSearchParams({
			from: params.from,
			to: params.to
		});

		if (params.applianceType) {
			query.set('applianceType', params.applianceType);
		}

		return apiFetch<HomeDashboardResponse>(
			`/analytics/homes/${homeId}/dashboard?${query.toString()}`,
			{ token }
		);
	},

	analystDashboard: (token: string, body: AnalystDashboardRequest) =>
		apiFetch<AnalystDashboardResponse>('/analytics/analyst/dashboard', {
			method: 'POST',
			token,
			body
		})
};

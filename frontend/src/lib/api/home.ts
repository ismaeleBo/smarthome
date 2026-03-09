import { apiFetch } from './http';
import type {
	HomeResponse,
	ConfigureHomeRequest,
	AssignHeadRequest,
	AssignAnalystHomesRequest,
	InvitationStatusResponse,
	CreateInvitationRequest,
	CreateInvitationResponse,
	InvitationResponse
} from '$lib/contracts/home';

export const HomeApi = {
	myHomes: (token: string) => apiFetch<HomeResponse[]>('/home/me/homes', { token }),

	allHomes: (token: string) => apiFetch<HomeResponse[]>('/home/homes', { token }),

	getHome: (token: string, homeId: number) =>
		apiFetch<HomeResponse>(`/home/homes/${homeId}`, { token }),

	configureHome: (token: string, homeId: number, body: ConfigureHomeRequest) =>
		apiFetch<void>(`/home/admin/homes/${homeId}/configure`, { method: 'POST', token, body }),

	disableHome: (token: string, homeId: number) =>
		apiFetch<void>(`/home/admin/homes/${homeId}/disable`, { method: 'POST', token }),

	reactivateHome: (token: string, homeId: number) =>
		apiFetch<void>(`/home/admin/homes/${homeId}/reactivate`, { method: 'POST', token }),

	assignHead: (token: string, homeId: number, body: AssignHeadRequest) =>
		apiFetch<void>(`/home/admin/homes/${homeId}/head`, { method: 'POST', token, body }),

	invitationStatus: (invitationToken: string) =>
		apiFetch<InvitationStatusResponse>(`/home/invitations/${encodeURIComponent(invitationToken)}`),

	createInvitation: (token: string, body: CreateInvitationRequest) =>
		apiFetch<CreateInvitationResponse>('/home/head/invitations', { method: 'POST', token, body }),

	revokeInvitation: (token: string, invitationToken: string) =>
		apiFetch<void>(`/home/head/invitations/${encodeURIComponent(invitationToken)}/revoke`, {
			method: 'POST',
			token
		}),

	invitationsForHome: (token: string, homeId: number) =>
		apiFetch<InvitationResponse[]>(`/home/head/invitations/${homeId}`, { token }),

	assignAnalystHomes: (token: string, body: AssignAnalystHomesRequest) =>
		apiFetch<void>(`/home/admin/homes/analysts/assign`, { method: 'PUT', token, body }),

	getAnalystAssignedHomes: (token: string, analystUserId: string) =>
		apiFetch<number[]>(`/home/admin/homes/analysts/${analystUserId}/homes`, { token })
};

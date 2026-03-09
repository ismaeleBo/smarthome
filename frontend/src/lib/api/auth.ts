import { apiFetch } from './http';
import type {
	LoginRequest,
	TokenResponse,
	UserSummaryResponse,
	ChangePasswordRequest,
	ProvisionUserRequest,
	ProvisionUserResponse,
	Role
} from '$lib/contracts/auth';

export const AuthApi = {
	login: (body: LoginRequest) => apiFetch<TokenResponse>('/auth/login', { method: 'POST', body }),

	me: (token: string) => apiFetch<UserSummaryResponse>('/auth/me', { token }),

	changePassword: (token: string, body: ChangePasswordRequest) =>
		apiFetch<void>('/auth/change-password', { method: 'POST', token, body }),

	provisionUser: (token: string, body: ProvisionUserRequest) =>
		apiFetch<ProvisionUserResponse>('/auth/internal/users', { method: 'POST', token, body }),

	adminUsers: (token: string) => apiFetch<UserSummaryResponse[]>('/auth/admin/users', { token }),

	adminUsersByRole: (token: string, role: Role) =>
		apiFetch<UserSummaryResponse[]>(`/auth/admin/users?role=${encodeURIComponent(role)}`, {
			token
		}),

	adminHeads: (token: string) =>
		apiFetch<UserSummaryResponse[]>('/auth/admin/users/heads', { token }),

	acceptInvitation: (token: string, invitationToken: string) =>
		apiFetch<void>('/auth/accept', { method: 'POST', token, body: { token: invitationToken } }),

	registerMember: (body: {
		token: string;
		email: string;
		firstName: string;
		lastName: string;
		dateOfBirth: string;
		password: string;
	}) => apiFetch<void>('/auth/register-member', { method: 'POST', body }),

	activateAccount: (body: { token: string; password: string }) =>
		apiFetch<void>('/auth/activate-account', { method: 'POST', body })
};

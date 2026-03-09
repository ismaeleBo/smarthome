export type HomeStatus = 'ACTIVE' | 'DISABLED' | string;

export type HomeResponse = {
	homeId: number;
	status: HomeStatus;
	address: string;
	streetNumber: string;
	city: string;
	pricePerKwh: number;
	headUserId: string | null;
};

export type ConfigureHomeRequest = {
	address: string;
	streetNumber: string;
	city: string;
	pricePerKwh: number;
};

export type AssignHeadRequest = {
	headUserId: string;
};

export type AssignAnalystHomesRequest = {
	analystUserId: string;
	homeIds: number[];
};

export type CreateInvitationRequest = {
	homeId: number;
	email: string;
};

export type CreateInvitationResponse = {
	expiresAt: string;
};

export type InvitationStatusResponse = {
	status: string;
};

export type InvitationStatus = 'VALID' | 'EXPIRED' | 'CONSUMED' | 'NOT_FOUND' | 'REVOKED';

export type InvitationResponse = {
	token: string;
	email: string;
	expiresAt: string;
	status: InvitationStatus;
};

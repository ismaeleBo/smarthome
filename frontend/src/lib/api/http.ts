import type { ApiError } from '$lib/contracts/common';
import { PUBLIC_API_BASE_URL } from '$env/static/public';

export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';

export class HttpError extends Error {
	status: number;
	apiError?: ApiError;

	constructor(status: number, message: string, apiError?: ApiError) {
		super(message);
		this.status = status;
		this.apiError = apiError;
	}
}

function getBaseUrl(): string {
	const base = PUBLIC_API_BASE_URL as string | undefined;
	if (!base) throw new Error('Missing PUBLIC_API_BASE_URL');
	return base.replace(/\/$/, '');
}

export async function apiFetch<T>(
	path: string,
	opts?: {
		method?: HttpMethod;
		token?: string | null;
		body?: unknown;
		headers?: Record<string, string>;
	}
): Promise<T> {
	const url = `${getBaseUrl()}${path.startsWith('/') ? '' : '/'}${path}`;

	const headers: Record<string, string> = {
		Accept: 'application/json',
		...(opts?.headers ?? {})
	};

	if (opts?.body !== undefined) headers['Content-Type'] = 'application/json';
	if (opts?.token) headers['Authorization'] = `Bearer ${opts.token}`;

	const res = await fetch(url, {
		method: opts?.method ?? 'GET',
		headers,
		body: opts?.body !== undefined ? JSON.stringify(opts.body) : undefined
	});

	console.log(`API ${opts?.method ?? 'GET'} ${url} -> ${res}`);

	if (res.ok) {
		if (res.status === 204) return undefined as T;

		const contentType = res.headers.get('content-type') ?? '';
		if (!contentType.includes('application/json')) {
			return undefined as unknown as T;
		}
		return (await res.json()) as T;
	}

	let apiError: ApiError | undefined;
	try {
		const contentType = res.headers.get('content-type') ?? '';
		if (contentType.includes('application/json')) {
			apiError = (await res.json()) as ApiError;
		}
	} catch {}

	const message =
		apiError?.message ??
		(res.status === 401
			? 'Not authenticated'
			: res.status === 403
				? 'Not authenticated'
				: 'Network error');

	throw new HttpError(res.status, message, apiError);
}

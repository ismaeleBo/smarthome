import { goto } from '$app/navigation';
import type { UserSummaryResponse, Role } from '$lib/contracts/auth';
import type { HomeResponse } from '$lib/contracts/home';
import { AuthApi } from '$lib/api/auth';
import { HomeApi } from '$lib/api/home';
import { HttpError } from '$lib/api/http';

const TOKEN_KEY = 'smarthome.token';
const SELECTED_HOME_KEY = 'smarthome.selectedHomeId';

type SessionState = {
	token: string | null;
	user: UserSummaryResponse | null;
	homes: HomeResponse[];
	selectedHomeId: number | null;
	bootstrapped: boolean;
};

function roleLanding(role: Role): string {
	return role === 'ADMIN' ? '/app/admin' : '/app/home';
}

export function createSession() {
	let state = $state<SessionState>({
		token: null,
		user: null,
		homes: [],
		selectedHomeId: null,
		bootstrapped: false
	});

	let bootstrapPromise: Promise<void> | null = $state(null);

	function setToken(token: string | null) {
		state.token = token;
		if (token) localStorage.setItem(TOKEN_KEY, token);
		else localStorage.removeItem(TOKEN_KEY);
	}

	function setSelectedHomeId(homeId: number | null) {
		state.selectedHomeId = homeId;
		if (homeId != null) localStorage.setItem(SELECTED_HOME_KEY, String(homeId));
		else localStorage.removeItem(SELECTED_HOME_KEY);
	}

	function loadFromStorage() {
		const t = localStorage.getItem(TOKEN_KEY);
		state.token = t && t.trim().length ? t : null;

		const saved = localStorage.getItem(SELECTED_HOME_KEY);
		const parsed = saved ? Number(saved) : NaN;
		state.selectedHomeId = Number.isFinite(parsed) ? parsed : null;
	}

	async function refreshMe() {
		if (!state.token) throw new Error('Missing token');
		state.user = await AuthApi.me(state.token);
	}

	async function refreshHomes() {
		if (!state.token) throw new Error('Missing token');
		state.homes = await HomeApi.myHomes(state.token);

		if (!state.homes.length) {
			setSelectedHomeId(null);
			return;
		}

		const stillOk =
			state.selectedHomeId != null && state.homes.some((h) => h.homeId === state.selectedHomeId);

		if (!stillOk) setSelectedHomeId(state.homes[0].homeId);
	}

	async function doBootstrap() {
		loadFromStorage();

		if (!state.token) {
			state.user = null;
			state.homes = [];
			state.selectedHomeId = null;
			state.bootstrapped = true;
			return;
		}

		try {
			await refreshMe();
			await refreshHomes();
		} catch (e) {
			if (e instanceof HttpError && (e.status === 401 || e.status === 403)) {
				logout(false);
			} else {
				throw e;
			}
		} finally {
			state.bootstrapped = true;
		}
	}

	async function bootstrap() {
		if (state.bootstrapped) return;

		if (bootstrapPromise) {
			await bootstrapPromise;
			return;
		}

		bootstrapPromise = doBootstrap();

		try {
			await bootstrapPromise;
		} finally {
			bootstrapPromise = null;
		}
	}

	async function login(email: string, password: string, redirectTo?: string) {
		const tok = await AuthApi.login({ email, password });
		setToken(tok.accessToken);

		state.bootstrapped = false;
		await bootstrap();

		if (!state.user) {
			throw new Error("Impossibile caricare l'utente autenticato");
		}

		await goto(redirectTo || roleLanding(state.user.role));
	}

	function logout(redirect = true) {
		setToken(null);
		state.user = null;
		state.homes = [];
		setSelectedHomeId(null);
		state.bootstrapped = true;
		if (redirect) void goto('/login');
	}

	function selectHome(homeId: number) {
		setSelectedHomeId(homeId);
	}

	return {
		get state() {
			return state;
		},
		bootstrap,
		login,
		logout,
		refreshMe,
		refreshHomes,
		selectHome
	};
}

export const session = createSession();

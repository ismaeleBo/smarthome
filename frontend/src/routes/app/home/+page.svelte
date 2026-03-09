<script lang="ts">
	import { onMount } from 'svelte';
	import { session } from '$lib/stores/session.svelte';
	import { HomeApi } from '$lib/api/home';
	import { HttpError } from '$lib/api/http';

	import type { HomeResponse } from '$lib/contracts/home';

	import HomeSelector from '$lib/components/home/HomeSelector.svelte';
	import HomeDetailsCard from '$lib/components/home/HomeDetailsCard.svelte';

	let detailsLoading = $state(false);
	let detailsError: string | null = $state(null);
	let selectedHomeDetails: HomeResponse | null = $state(null);

	const s = session.state;

	function parseError(error: unknown, fallback = 'Operazione non riuscita'): string {
		if (error instanceof HttpError) return error.apiError?.message ?? error.message;
		if (error instanceof Error) return error.message;
		return fallback;
	}

	async function loadSelectedHomeDetails() {
		detailsError = null;
		selectedHomeDetails = null;

		if (!s.token || s.selectedHomeId == null) return;

		detailsLoading = true;
		try {
			selectedHomeDetails = await HomeApi.getHome(s.token, s.selectedHomeId);
		} catch (error) {
			detailsError = parseError(error, 'Impossibile caricare i dettagli della home');
		} finally {
			detailsLoading = false;
		}
	}

	async function handleHomeChange(homeId: number) {
		session.selectHome(homeId);
		await loadSelectedHomeDetails();
	}

	onMount(loadSelectedHomeDetails);
</script>

<div class="space-y-6 p-6">
	<div class="flex items-center justify-between">
		<div>
			<h1 class="text-xl font-semibold">
				{#if s.user?.role === 'HEAD'}
					Dashboard Head
				{:else if s.user?.role === 'ANALYST'}
					Dashboard Analyst
				{:else if s.user?.role === 'MEMBER'}
					Dashboard Member
				{:else}
					Dashboard Home
				{/if}
			</h1>
			<p class="text-sm opacity-70">
				Utente: {s.user?.firstName}
				{s.user?.lastName} ({s.user?.role})
			</p>
		</div>

		<div class="flex items-center gap-2">
			{#if s.user?.role === 'HEAD'}
				<a href="/app/head/invitations" class="rounded-xl border px-3 py-2 text-sm">
					Manage Invitations
				</a>
			{/if}

			<button class="rounded-xl border px-3 py-2 text-sm" onclick={() => session.logout()}>
				Logout
			</button>
		</div>
	</div>

	{#if s.homes.length > 0}
		<section class="rounded-2xl border bg-white p-5">
			<HomeSelector
				homes={s.homes}
				selectedHomeId={s.selectedHomeId}
				label="Selected Home"
				onChange={handleHomeChange}
			/>
		</section>

		<HomeDetailsCard loading={detailsLoading} error={detailsError} home={selectedHomeDetails} />

		<section class="rounded-2xl border bg-white p-4">
			<p class="text-sm opacity-70">
				Qui poi aggiungeremo i widget specifici del ruolo e i dati analytics.
			</p>
		</section>
	{:else}
		<div>
			<p class="text-sm opacity-70">No homes available</p>
		</div>
	{/if}
</div>

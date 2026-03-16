<script lang="ts">
	import { onMount } from 'svelte';
	import { session } from '$lib/stores/session.svelte';
	import { HomeApi } from '$lib/api/home';
	import { HttpError } from '$lib/api/http';

	import type { HomeResponse, InvitationResponse } from '$lib/contracts/home';

	import HomeSelector from '$lib/components/home/HomeSelector.svelte';
	import HomeDetailsCard from '$lib/components/home/HomeDetailsCard.svelte';
	import HeadInvitationForm from '$lib/components/head/HeadInvitationForm.svelte';
	import HeadInvitationsTable from '$lib/components/head/HeadInvitationsTable.svelte';

	let detailsLoading = $state(false);
	let detailsError: string | null = $state(null);
	let selectedHomeDetails: HomeResponse | null = $state(null);

	let inviteEmail = $state('');
	let inviteLoading = $state(false);
	let inviteError: string | null = $state(null);
	let inviteSuccess: string | null = $state(null);
	let invitationExpiresAt: string | null = $state(null);

	let invitationsLoading = $state(false);
	let invitationsError: string | null = $state(null);
	let invitations: InvitationResponse[] = $state([]);

	let revokingToken: string | null = $state(null);

	const s = session.state;

	function parseError(error: unknown, fallback = 'Operation failed'): string {
		if (error instanceof HttpError) return error.apiError?.message ?? error.message;
		if (error instanceof Error) return error.message;
		return fallback;
	}

	function resetInviteBanner() {
		inviteError = null;
		inviteSuccess = null;
		invitationExpiresAt = null;
	}

	function resetInvitationsError() {
		invitationsError = null;
	}

	async function loadSelectedHomeDetails() {
		detailsError = null;
		selectedHomeDetails = null;

		if (!s.token || s.selectedHomeId == null) return;

		detailsLoading = true;
		try {
			selectedHomeDetails =
				(await HomeApi.myHomes(s.token)).find((h) => h.homeId === s.selectedHomeId) ?? null;
		} catch (error) {
			detailsError = parseError(error, 'Unable to load home details');
		} finally {
			detailsLoading = false;
		}
	}

	async function loadInvitationsForSelectedHome() {
		resetInvitationsError();
		invitations = [];

		if (!s.token || s.selectedHomeId == null) return;

		invitationsLoading = true;
		try {
			const result = await HomeApi.invitationsForHome(s.token, s.selectedHomeId);
			invitations = result.sort((a, b) => {
				const ta = new Date(a.expiresAt).getTime();
				const tb = new Date(b.expiresAt).getTime();
				return tb - ta;
			});
		} catch (error) {
			invitationsError = parseError(error, 'Unable to load invitations for the selected home');
		} finally {
			invitationsLoading = false;
		}
	}

	async function reloadPageContext() {
		await loadSelectedHomeDetails();
		await loadInvitationsForSelectedHome();
	}

	async function handleHomeChange(homeId: number) {
		session.selectHome(homeId);
		resetInviteBanner();
		await reloadPageContext();
	}

	async function handleInviteSubmit(email: string) {
		resetInviteBanner();

		if (!s.token) {
			inviteError = 'Invalid session';
			return;
		}

		if (s.selectedHomeId == null) {
			inviteError = 'Select a home';
			return;
		}

		if (!email.trim()) {
			inviteError = 'Enter a valid email';
			return;
		}

		inviteLoading = true;

		try {
			const res = await HomeApi.createInvitation(s.token, {
				homeId: s.selectedHomeId,
				email: email.trim()
			});

			inviteSuccess = `Invitation created successfully for ${email.trim()}`;
			invitationExpiresAt = res.expiresAt;
			inviteEmail = '';

			await loadInvitationsForSelectedHome();
		} catch (error) {
			inviteError = parseError(error, 'Failed to create invitation');
		} finally {
			inviteLoading = false;
		}
	}

	async function handleRevoke(token: string) {
		resetInvitationsError();

		if (!s.token) {
			invitationsError = 'Invalid session';
			return;
		}

		revokingToken = token;

		try {
			await HomeApi.revokeInvitation(s.token, token);
			await loadInvitationsForSelectedHome();
		} catch (error) {
			invitationsError = parseError(error, 'Failed to revoke invitation');
		} finally {
			revokingToken = null;
		}
	}

	onMount(reloadPageContext);

	$effect(() => {
		if (s.selectedHomeId != null) {
			resetInviteBanner();
			void reloadPageContext();
		}
	});
</script>

<div class="space-y-6 p-6">
	{#if s.homes.length > 0}
		<HomeDetailsCard loading={detailsLoading} error={detailsError} home={selectedHomeDetails} />

		<div class="grid gap-6 xl:grid-cols-[1fr,1.4fr]">
			<HeadInvitationForm
				loading={inviteLoading}
				error={inviteError}
				success={inviteSuccess}
				expiresAt={invitationExpiresAt}
				bind:email={inviteEmail}
				onSubmit={handleInviteSubmit}
			/>

			<HeadInvitationsTable
				loading={invitationsLoading}
				error={invitationsError}
				{invitations}
				{revokingToken}
				onRevoke={handleRevoke}
			/>
		</div>
	{:else}
		<div>
			<p class="text-sm opacity-70">No homes available</p>
		</div>
	{/if}
</div>

<script lang="ts">
	import { goto, invalidateAll } from '$app/navigation';
	import { onMount } from 'svelte';

	import { AuthApi } from '$lib/api/auth';
	import { HomeApi } from '$lib/api/home';
	import { HttpError } from '$lib/api/http';
	import { session } from '$lib/stores/session.svelte';

	import type { RegisterMemberFormData } from '$lib/components/auth/RegisterMemberForm.svelte';
	import RegisterMemberForm from '$lib/components/auth/RegisterMemberForm.svelte';
	import { page } from '$app/state';

	let token: string | null = $state(null);

	let checking = $state(true);
	let tokenError: string | null = $state(null);
	let invitationStatus: string | null = $state(null);

	let registerLoading = $state(false);
	let registerError: string | null = $state(null);
	let registerSuccess: string | null = $state(null);

	let acceptLoading = $state(false);
	let acceptError: string | null = $state(null);
	let acceptSuccess: string | null = $state(null);

	let email = $state('');
	let firstName = $state('');
	let lastName = $state('');
	let dateOfBirth = $state('');
	let password = $state('');
	let confirmPassword = $state('');

	function parseError(error: unknown, fallback = 'Operation failed'): string {
		if (error instanceof HttpError) return error.apiError?.message ?? error.message;
		if (error instanceof Error) return error.message;
		return fallback;
	}

	onMount(async () => {
		await session.bootstrap();

		token = page.url.searchParams.get('token');
		if (!token) {
			tokenError = 'Missing activation token';
			checking = false;
			return;
		}

		try {
			const res = await HomeApi.invitationStatus(token);
			invitationStatus = res.status;

			if (res.status !== 'VALID') {
				tokenError = `Invitation not valid: ${res.status}`;
			}
		} catch (e) {
			tokenError = parseError(e, 'Unable to verify invitation token');
		} finally {
			checking = false;
		}
	});

	async function handleRegisterSubmit(data: RegisterMemberFormData) {
		registerError = null;
		registerSuccess = null;

		if (!token) {
			registerError = 'Missing invitation token';
			return;
		}

		if (data.password !== data.confirmPassword) {
			registerError = 'The passwords do not match';
			return;
		}

		registerLoading = true;

		try {
			await AuthApi.registerMember({
				token,
				email: data.email.trim(),
				firstName: data.firstName.trim(),
				lastName: data.lastName.trim(),
				dateOfBirth: data.dateOfBirth,
				password: data.password
			});

			registerSuccess = 'Registration completed. You can now log in.';

			email = '';
			firstName = '';
			lastName = '';
			dateOfBirth = '';
			password = '';
			confirmPassword = '';

			setTimeout(() => {
				void goto('/login');
			}, 1400);
		} catch (e) {
			registerError = parseError(e, 'Member registration failed');
		} finally {
			registerLoading = false;
		}
	}

	async function acceptInvitation() {
		acceptError = null;
		acceptSuccess = null;

		if (!token) {
			acceptError = 'Missing invitation token';
			return;
		}

		if (!session.state.token || !session.state.user) {
			acceptError = 'You must be logged in as a MEMBER';
			return;
		}

		if (session.state.user.role !== 'MEMBER') {
			acceptError = 'Only a MEMBER user can accept this invitation';
			return;
		}

		acceptLoading = true;

		try {
			await AuthApi.acceptInvitation(session.state.token, token);
			acceptSuccess = 'Invitation accepted successfully.';

			await session.refreshHomes();

			setTimeout(() => {
				void goto('/app/home');
			}, 1200);
		} catch (e) {
			acceptError = parseError(e, 'Invitation acceptance failed');
		} finally {
			acceptLoading = false;
		}
	}
</script>

<div class="flex min-h-screen items-center justify-center p-6">
	<div class="w-full max-w-2xl">
		{#if checking}
			<div class="rounded-2xl border bg-white p-6 text-sm opacity-70">Verifying invitation...</div>
		{:else if tokenError}
			<div class="rounded-2xl border bg-white p-6">
				<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
					{tokenError}
				</div>
			</div>
		{:else if session.state.user}
			{#if session.state.user.role === 'MEMBER'}
				<section class="space-y-4 rounded-2xl border bg-white p-6">
					<div>
						<h1 class="text-xl font-semibold">Accept Invitation</h1>
					</div>

					{#if acceptError}
						<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
							{acceptError}
						</div>
					{/if}

					{#if acceptSuccess}
						<div class="rounded-xl border border-green-200 bg-green-50 p-3 text-sm">
							{acceptSuccess}
						</div>
					{/if}

					<div class="text-sm">
						<div><span class="font-medium">User:</span> {session.state.user.email}</div>
						<div><span class="font-medium">Invitation Status:</span> {invitationStatus}</div>
					</div>

					<button
						class="rounded-xl bg-black px-4 py-2 text-white disabled:opacity-50"
						onclick={acceptInvitation}
						disabled={acceptLoading}
					>
						{acceptLoading ? 'Accepting...' : 'Accept Invitation'}
					</button>
				</section>
			{:else}
				<section class="rounded-2xl border bg-white p-6">
					<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
						You are logged in as {session.state.user.role}. Only a MEMBER user can accept this
						invitation directly. Log out and register, or log in with a MEMBER account.
					</div>
					<button class="mt-2 rounded-xl border px-3 py-2 text-sm" onclick={() => session.logout()}>
						Logout
					</button>
				</section>
			{/if}
		{:else}
			<div class="space-y-4">
				<RegisterMemberForm
					loading={registerLoading}
					error={registerError}
					success={registerSuccess}
					bind:email
					bind:firstName
					bind:lastName
					bind:dateOfBirth
					bind:password
					bind:confirmPassword
					onSubmit={handleRegisterSubmit}
				/>
				<section class="rounded-2xl border bg-white p-6">
					<p class="text-sm opacity-70">Do you have already an account?</p>
					<a
						class="mt-3 inline-block rounded-xl border px-4 py-2 text-sm"
						href={`/login?redirect=${encodeURIComponent(`/invitation?token=${token}`)}`}
					>
						Log in to accept the invitation
					</a>
				</section>
			</div>
		{/if}
	</div>
</div>

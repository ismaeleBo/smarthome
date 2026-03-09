<script lang="ts">
	import { goto } from '$app/navigation';
	import { onMount } from 'svelte';

	import { AuthApi } from '$lib/api/auth';
	import { HomeApi } from '$lib/api/home';
	import { HttpError } from '$lib/api/http';
	import { session } from '$lib/stores/session.svelte';

	import ActivateAccountForm from '$lib/components/auth/ActivateAccountForm.svelte';
	import { page } from '$app/state';

	let token: string | null = $state(null);

	let checking = $state(true);
	let tokenStatusError: string | null = $state(null);

	let loading = $state(false);
	let error: string | null = $state(null);
	let success: string | null = $state(null);

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
			tokenStatusError = 'Missing activation token';
			checking = false;
			return;
		}

		try {
			const res = await HomeApi.invitationStatus(token).catch(() => null);
			void res;
		} finally {
			checking = false;
		}
	});

	async function handleSubmit(password: string, confirmPassword: string) {
		error = null;
		success = null;

		if (!token) {
			error = 'Missing activation token';
			return;
		}

		if (!password.trim()) {
			error = 'Please enter a password';
			return;
		}

		if (password !== confirmPassword) {
			error = 'The passwords do not match';
			return;
		}

		loading = true;

		try {
			await AuthApi.activateAccount({
				token,
				password
			});

			success = 'Account activated successfully. You will be redirected to the login page shortly.';
			password = '';
			confirmPassword = '';

			setTimeout(() => {
				void goto('/login');
			}, 1200);
		} catch (e) {
			error = parseError(e, 'Account activation failed');
		} finally {
			loading = false;
		}
	}
</script>

<div class="flex min-h-screen items-center justify-center p-6">
	<div class="w-full max-w-lg">
		{#if checking}
			<div class="rounded-2xl border bg-white p-6 text-sm opacity-70">Verifying token...</div>
		{:else if tokenStatusError}
			<div class="rounded-2xl border bg-white p-6">
				<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
					{tokenStatusError}
				</div>
			</div>
		{:else}
			<ActivateAccountForm
				{loading}
				{error}
				{success}
				bind:password
				bind:confirmPassword
				onSubmit={handleSubmit}
			/>
		{/if}
	</div>
</div>

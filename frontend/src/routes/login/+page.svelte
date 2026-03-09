<script lang="ts">
	import { session } from '$lib/stores/session.svelte';
	import { HttpError } from '$lib/api/http';

	let email = $state('');
	let password = $state('');
	let loading = $state(false);
	let errorMsg: string | null = $state(null);

	async function submit(event: SubmitEvent) {
		event.preventDefault();
		loading = true;
		errorMsg = null;
		try {
			await session.login(email.trim(), password);
		} catch (e) {
			console.log(e, 'errr');

			if (e instanceof HttpError) errorMsg = e.apiError?.message ?? e.message;
			else errorMsg = 'Unexpected error occurred.';
		} finally {
			loading = false;
		}
	}
</script>

<div class="flex min-h-screen items-center justify-center p-6">
	<div class="w-full max-w-md rounded-2xl border bg-white p-6 shadow-sm">
		<h1 class="text-xl font-semibold">Login</h1>
		<p class="mt-1 text-sm opacity-70">Accedi con le credenziali.</p>

		{#if errorMsg}
			<div class="mt-4 rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
				{errorMsg}
			</div>
		{/if}

		<form class="mt-4 space-y-3" onsubmit={submit}>
			<div>
				<label for="email" class="text-sm font-medium">Email</label>
				<input
					name="email"
					id="email"
					class="mt-1 w-full rounded-xl border p-2"
					type="email"
					bind:value={email}
					autocomplete="email"
					required
				/>
			</div>

			<div>
				<label for="password" class="text-sm font-medium">Password</label>
				<input
					name="password"
					id="password"
					class="mt-1 w-full rounded-xl border p-2"
					type="password"
					bind:value={password}
					autocomplete="current-password"
					required
				/>
			</div>

			<button
				class="w-full rounded-xl bg-black p-2 text-white disabled:opacity-50"
				type="submit"
				disabled={loading}
			>
				{loading ? 'Logging in...' : 'Enter'}
			</button>
		</form>
	</div>
</div>

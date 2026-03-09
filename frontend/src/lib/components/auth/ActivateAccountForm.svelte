<script lang="ts">
	type Props = {
		loading: boolean;
		error: string | null;
		success: string | null;
		password: string;
		confirmPassword: string;
		onSubmit: (password: string, confirmPassword: string) => void;
	};

	let {
		loading,
		error,
		success,
		password = $bindable(),
		confirmPassword = $bindable(),
		onSubmit
	}: Props = $props();

	function handleSubmit() {
		onSubmit(password, confirmPassword);
	}
</script>

<section class="space-y-4 rounded-2xl border bg-white p-6">
	<div>
		<h1 class="text-xl font-semibold">Activate Account</h1>
		<p class="text-sm opacity-70">Set your password to complete your account activation.</p>
	</div>

	{#if error}
		<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
			{error}
		</div>
	{/if}

	{#if success}
		<div class="rounded-xl border border-green-200 bg-green-50 p-3 text-sm">
			{success}
		</div>
	{/if}

	<div class="space-y-3">
		<div>
			<label for="password" class="text-sm font-medium">Password</label>
			<input
				name="password"
				id="password"
				class="mt-1 w-full rounded-xl border p-2"
				type="password"
				bind:value={password}
				autocomplete="new-password"
			/>
		</div>

		<div>
			<label for="confirmPassword" class="text-sm font-medium">Confirm Password</label>
			<input
				name="confirmPassword"
				id="confirmPassword"
				class="mt-1 w-full rounded-xl border p-2"
				type="password"
				bind:value={confirmPassword}
				autocomplete="new-password"
			/>
		</div>

		<button
			class="rounded-xl bg-black px-4 py-2 text-white disabled:opacity-50"
			onclick={handleSubmit}
			disabled={loading}
		>
			{loading ? 'Activating...' : 'Activate Account'}
		</button>
	</div>
</section>

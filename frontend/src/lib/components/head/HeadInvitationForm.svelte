<script lang="ts">
	type Props = {
		loading: boolean;
		error: string | null;
		success: string | null;
		expiresAt: string | null;
		email: string;
		onSubmit: (email: string) => void;
	};

	let { loading, error, success, expiresAt, email = $bindable(), onSubmit }: Props = $props();

	function formatDateTime(value: string | null): string {
		if (!value) return '-';

		const date = new Date(value);
		if (Number.isNaN(date.getTime())) return value;

		return new Intl.DateTimeFormat('it-IT', {
			dateStyle: 'short',
			timeStyle: 'short'
		}).format(date);
	}

	function handleSubmit() {
		onSubmit(email);
	}
</script>

<section class="space-y-4 rounded-2xl border bg-white p-5">
	<div>
		<h2 class="text-lg font-semibold">Invita un Member</h2>
		<p class="text-sm opacity-70">Crea un invito per la home attualmente selezionata.</p>
	</div>

	{#if error}
		<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
			{error}
		</div>
	{/if}

	{#if success}
		<div class="space-y-1 rounded-xl border border-green-200 bg-green-50 p-3 text-sm">
			<div>{success}</div>
			{#if expiresAt}
				<div class="opacity-80">Scadenza invito: {formatDateTime(expiresAt)}</div>
			{/if}
		</div>
	{/if}

	<div class="space-y-3">
		<div>
			<label for="email" class="text-sm font-medium">Email</label>
			<input
				name="email"
				id="email"
				class="mt-1 w-full rounded-xl border p-2"
				type="email"
				bind:value={email}
				placeholder="member@example.com"
			/>
		</div>

		<button
			class="rounded-xl bg-black px-4 py-2 text-white disabled:opacity-50"
			onclick={handleSubmit}
			disabled={loading}
		>
			{loading ? 'Sending...' : 'Create Invitation'}
		</button>
	</div>
</section>

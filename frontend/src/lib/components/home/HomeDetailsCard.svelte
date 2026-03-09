<script lang="ts">
	import type { HomeResponse } from '$lib/contracts/home';

	type Props = {
		loading: boolean;
		error: string | null;
		home: HomeResponse | null;
	};

	let { loading, error, home }: Props = $props();
</script>

<section class="space-y-4 rounded-2xl border bg-white p-5">
	<div>
		<h2 class="text-lg font-semibold">Home Details</h2>
		<p class="text-sm opacity-70">Base information about the selected home.</p>
	</div>

	{#if loading}
		<p class="text-sm opacity-70">Loading home details...</p>
	{:else if error}
		<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
			{error}
		</div>
	{:else if home}
		<div class="grid gap-3 text-sm sm:grid-cols-2 xl:grid-cols-3">
			<div><span class="font-medium">Home ID:</span> {home.homeId}</div>
			<div><span class="font-medium">Status:</span> {home.status}</div>
			<div><span class="font-medium">Address:</span> {home.address}</div>
			<div><span class="font-medium">Street number:</span> {home.streetNumber}</div>
			<div><span class="font-medium">City:</span> {home.city}</div>
			<div><span class="font-medium">Price per kWh:</span> {home.pricePerKwh}</div>
		</div>
	{:else}
		<p class="text-sm opacity-70">Nessuna home selezionata.</p>
	{/if}
</section>

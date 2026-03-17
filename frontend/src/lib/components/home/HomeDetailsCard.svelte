<script lang="ts">
	import type { HomeResponse } from '$lib/contracts/home';

	type Props = {
		loading?: boolean;
		error?: string | null;
		home: HomeResponse | null;
	};

	let { loading, error, home }: Props = $props();
</script>

<section>
	<div>
		<h2 class="text-lg font-semibold">Home Details</h2>
	</div>

	{#if loading}
		<p class="text-sm opacity-70">Loading home details...</p>
	{:else if error}
		<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
			{error}
		</div>
	{:else if home}
		<div class="text-sm">
			<div>
				<span class="font-medium">Address: </span>
				{home.address}
				{home.streetNumber}, {home.city}
			</div>
			<div><span class="font-medium">Price per kWh:</span> {home.pricePerKwh}€</div>
		</div>
	{:else}
		<p class="text-sm opacity-70">No home selected.</p>
	{/if}
</section>

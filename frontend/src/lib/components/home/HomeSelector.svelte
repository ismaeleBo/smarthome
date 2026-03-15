<script lang="ts">
	import type { HomeResponse } from '$lib/contracts/home';

	type Props = {
		homes: HomeResponse[];
		selectedHomeId: number | null;
		label?: string;
		onChange: (homeId: number) => void;
	};

	let { homes, selectedHomeId, onChange, label = 'Home selezionata' }: Props = $props();

	function handleChange(e: Event) {
		const homeId = Number((e.currentTarget as HTMLSelectElement).value);
		onChange(homeId);
	}
</script>

<div>
	<label for="home" class="text-sm font-medium">Selected Home</label>
	<select
		name="home"
		id="home"
		class="mt-1 rounded-xl border p-2"
		bind:value={selectedHomeId}
		onchange={handleChange}
	>
		{#each homes as h}
			<option value={h.homeId}>Home #{h.homeId} - {h.status}</option>
		{/each}
	</select>
</div>

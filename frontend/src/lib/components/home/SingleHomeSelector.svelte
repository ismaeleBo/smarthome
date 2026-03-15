<script lang="ts">
	import type { HomeResponse } from '$lib/contracts/home';

	type Props = {
		homes: HomeResponse[];
		selectedHomeId: number | null;
		onChange: (homeId: number) => void;
	};

	let { homes, selectedHomeId, onChange }: Props = $props();
</script>

<div class="space-y-1">
	<label for="home" class="text-sm font-medium">Home</label>

	<select
		id="home"
		class="w-full rounded-xl border p-2"
		bind:value={selectedHomeId}
		onchange={(e) => onChange(Number((e.currentTarget as HTMLSelectElement).value))}
	>
		{#each homes as h}
			<option value={h.homeId}>
				Home #{h.homeId}
			</option>
		{/each}
	</select>
</div>

<script lang="ts">
	import { session } from '$lib/stores/session.svelte';

	type Props = {
		homeIds: number[];
		selectedHomeIds: number[];
		onToggle: (homeId: number, checked: boolean) => void;
		onSelectAll: () => void;
		onDeselectAll: () => void;
	};

	let { homeIds, selectedHomeIds, onToggle, onSelectAll, onDeselectAll }: Props = $props();

	const s = session.state;
</script>

<div class="space-y-2">
	<div class="flex items-center justify-between">
		<label for="homes" class="text-sm font-medium">Homes</label>

		<div class="flex gap-2 text-xs">
			<button class="rounded border px-2 py-1" onclick={onSelectAll}> Select all </button>

			<button class="rounded border px-2 py-1" onclick={onDeselectAll}> Deselect all </button>
		</div>
	</div>

	<div id="homes" class="max-h-52 space-y-2 overflow-auto rounded-xl border p-3">
		{#each homeIds as id}
			{@const home = s.homes.find((h) => h.homeId === id)}
			<label class="flex items-center gap-2 text-sm">
				<input
					type="checkbox"
					checked={selectedHomeIds.includes(id)}
					onchange={(e) => onToggle(id, (e.currentTarget as HTMLInputElement).checked)}
				/>
				<span>{`Home #${id} - ${home?.address} ${home?.streetNumber}, ${home?.city}`}</span>
			</label>
		{/each}
	</div>
</div>

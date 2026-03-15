<script lang="ts">
	type SummaryCard = {
		label: string;
		value: string;
		subtitle?: string;
	};

	type Props = {
		cards: SummaryCard[];
	};

	let { cards }: Props = $props();
</script>

<div class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
	{#each cards as card}
		<div class="rounded-2xl border bg-white p-4">
			<div class="text-xs uppercase tracking-wide opacity-60">{card.label}</div>
			<div class="mt-2 text-lg font-semibold">{card.value}</div>
			{#if card.subtitle}
				<div class="mt-1 text-sm opacity-70">{card.subtitle}</div>
			{/if}
		</div>
	{/each}
</div>
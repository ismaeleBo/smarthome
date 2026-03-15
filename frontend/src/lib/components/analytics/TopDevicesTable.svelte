<script lang="ts">
	import type { TopDeviceDto } from "$lib/contracts/analytics";

	type Props = {
		rows: TopDeviceDto[];
	};

	let { rows }: Props = $props();
</script>

<section class="rounded-2xl border bg-white p-5">
	<h3 class="text-lg font-semibold">Top devices</h3>

	<div class="mt-4 overflow-x-auto">
		<table class="w-full text-sm">
			<thead>
				<tr class="border-b text-left">
					<th class="py-2 pr-3">Device</th>
					<th class="py-2 pr-3">Consumption (kWh)</th>
					<th class="py-2 pr-3">Estimated cost</th>
					<th class="py-2 pr-3">Prevalent time band</th>
				</tr>
			</thead>
			<tbody>
				{#each rows as row}
					<tr class="border-b">
						<td class="py-3 pr-3">{row.applianceType}</td>
						<td class="py-3 pr-3">{row.totalConsumptionKwh.toFixed(2)}</td>
						<td class="py-3 pr-3">{row.estimatedCost.toFixed(2)}€</td>
						<td class="py-3 pr-3">{row.prevalentTimeBand}</td>
					</tr>
				{/each}
			</tbody>
		</table>
	</div>
</section>
<script lang="ts">
	import type { HeatingPeakDto } from "$lib/contracts/analytics";

	type Props = {
		rows: HeatingPeakDto[];
	};

	let { rows }: Props = $props();

	function formatDateTime(value: string): string {
		const date = new Date(value);
		if (Number.isNaN(date.getTime())) return value;

		return new Intl.DateTimeFormat("en-GB", {
			dateStyle: "short",
			timeStyle: "short"
		}).format(date);
	}
</script>

<section class="rounded-2xl border bg-white p-5">
	<h3 class="text-lg font-semibold">Heating peaks</h3>

	<div class="mt-4 overflow-x-auto">
		<table class="w-full text-sm">
			<thead>
				<tr class="border-b text-left">
					<th class="py-2 pr-3">Time</th>
					<th class="py-2 pr-3">Consumption (kWh)</th>
					<th class="py-2 pr-3">Outdoor temperature (°C)</th>
				</tr>
			</thead>
			<tbody>
				{#each rows as row}
					<tr class="border-b">
						<td class="py-3 pr-3">{formatDateTime(row.time)}</td>
						<td class="py-3 pr-3">{row.consumptionKwh.toFixed(2)}</td>
						<td class="py-3 pr-3">
							{row.outdoorTemperatureC == null ? "-" : row.outdoorTemperatureC.toFixed(1)}
						</td>
					</tr>
				{/each}
			</tbody>
		</table>
	</div>
</section>
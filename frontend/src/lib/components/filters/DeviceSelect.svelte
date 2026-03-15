<script lang="ts">
	import type { ApplianceTypeResponse } from '$lib/contracts/measurements';

	type Props = {
		devices: ApplianceTypeResponse[];
		selectedDevice: string | null;
		onChange: (device: string | null) => void;
	};

	let { devices, selectedDevice, onChange }: Props = $props();
</script>

<div class="space-y-1">
	<label for="device" class="text-sm font-medium">Device</label>

	<select
		id="device"
		class="w-full rounded-xl border p-2"
		bind:value={selectedDevice}
		onchange={(e) => {
			const value = (e.currentTarget as HTMLSelectElement).value;
			onChange(!value ? null : value);
		}}
	>
		<option value={null}>All devices</option>

		{#each devices as d}
			<option value={d.applianceType}>
				{d.applianceType}
			</option>
		{/each}
	</select>
</div>

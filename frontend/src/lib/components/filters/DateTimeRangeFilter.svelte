<script lang="ts">
	import type { DateTimeRange } from '$lib/utils/datetime';

	type Props = {
		range: DateTimeRange | null;
		min: string;
		max: string;
		onChange: (range: DateTimeRange) => void;
	};

	let { range, min, max, onChange }: Props = $props();

	function updateFrom(value: string) {
		if (!range) return;
		onChange({ ...range, from: value });
	}

	function updateTo(value: string) {
		if (!range) return;
		onChange({ ...range, to: value });
	}
</script>

<div class="grid gap-3 sm:grid-cols-2">
	<div>
		<label for="from" class="text-sm font-medium">From</label>

		<input
			type="datetime-local"
			id="from"
			class="mt-1 w-full rounded-xl border p-2"
			value={range?.from}
			{min}
			{max}
			onchange={(e) => updateFrom((e.currentTarget as HTMLInputElement).value)}
		/>
	</div>

	<div>
		<label for="to" class="text-sm font-medium">To</label>

		<input
			type="datetime-local"
			id="to"
			class="mt-1 w-full rounded-xl border p-2"
			value={range?.to}
			{min}
			{max}
			onchange={(e) => updateTo((e.currentTarget as HTMLInputElement).value)}
		/>
	</div>
</div>

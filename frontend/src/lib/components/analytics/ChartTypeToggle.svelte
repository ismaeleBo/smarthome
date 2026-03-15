<script lang="ts">
	type ChartTypeMode = "bar" | "line";

	type Props = {
		value: ChartTypeMode;
		onChange: (value: ChartTypeMode) => void;
	};

	let { value, onChange }: Props = $props();
</script>

<div class="flex items-center gap-2">
	<button
		class={`rounded-lg border px-3 py-1 text-xs ${value === "bar" ? "bg-black text-white" : ""}`}
		onclick={() => onChange("bar")}
	>
		Bar
	</button>

	<button
		class={`rounded-lg border px-3 py-1 text-xs ${value === "line" ? "bg-black text-white" : ""}`}
		onclick={() => onChange("line")}
	>
		Line
	</button>
</div>
<script lang="ts">
	import { onMount } from "svelte";
	import Chart from "chart.js/auto";

	type ChartTypeMode = "bar" | "line";

	type Props = {
		title: string;
		labels: string[];
		consumption: number[];
		costs: number[];
		mode: ChartTypeMode;
		height?: number;
	};

	let { title, labels, consumption, costs, mode, height = 320 }: Props = $props();

	let canvas: HTMLCanvasElement;
	let chart: Chart | null = null;

	function buildChart() {
		if (!canvas) return;

		if (chart) {
			chart.destroy();
		}

		chart = new Chart(canvas, {
			type: mode,
			data: {
				labels,
				datasets: [
					{
						label: "Consumption (kWh)",
						data: consumption,
						yAxisID: "y"
					},
					{
						label: "Estimated cost",
						data: costs,
						yAxisID: "y1"
					}
				]
			},
			options: {
				responsive: true,
				maintainAspectRatio: false,
				interaction: {
					mode: "index",
					intersect: false
				},
				scales: {
					y: {
						beginAtZero: true,
						title: {
							display: true,
							text: "Consumption (kWh)"
						}
					},
					y1: {
						beginAtZero: true,
						position: "right",
						grid: {
							drawOnChartArea: false
						},
						title: {
							display: true,
							text: "Estimated cost"
						}
					}
				},
				plugins: {
					title: {
						display: true,
						text: title
					},
					tooltip: {
						callbacks: {
							label(context) {
								const value = Number(context.raw ?? 0);
								if (context.datasetIndex === 0) {
									return `Consumption: ${value.toFixed(2)} kWh`;
								}
								return `Estimated cost: ${value.toFixed(2)}€`;
							}
						}
					}
				}
			}
		});
	}

	onMount(() => {
		buildChart();
		return () => chart?.destroy();
	});

	$effect(() => {
		labels;
		consumption;
		costs;
		mode;
		buildChart();
	});
</script>

<div class="rounded-2xl border bg-white p-4">
	<div class="relative" style={`height:${height}px`}>
		<canvas bind:this={canvas}></canvas>
	</div>
</div>
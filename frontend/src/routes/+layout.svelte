<script lang="ts">
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import { Toaster } from 'svelte-sonner';

	let { children } = $props();

	export const ssr = false;
	export const prerender = true;
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

<Toaster position="bottom-right" richColors />
{@render children()}

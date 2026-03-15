<script lang="ts">
	import { onMount, type Snippet } from 'svelte';
	import { goto } from '$app/navigation';
	import { session } from '$lib/stores/session.svelte';

	interface Props {
		children?: Snippet;
	}

	let { children }: Props = $props();

	let ready = $state(false);

	onMount(async () => {
		await session.bootstrap();

		if (!session.state.user) {
			await goto('/login');
			return;
		}

		if (session.state.user.role !== 'ANALYST') {
			await goto('/app/home');
			return;
		}

		ready = true;
	});
</script>

{#if ready}
	{@render children?.()}
{:else}
	<p class="p-6 text-sm opacity-70">Checking permissions...</p>
{/if}

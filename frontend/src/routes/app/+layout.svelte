<script lang="ts">
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { session } from '$lib/stores/session.svelte';
	import { page } from '$app/state';

	let { children } = $props();

	onMount(async () => {
		await session.bootstrap();

		if (!session.state.token || !session.state.user) {
			await goto('/login');
			return;
		}

		const path = page.url.pathname as string;
		if (path === '/app') {
			await goto(session.state.user.role === 'ADMIN' ? '/app/admin' : '/app/home');
		}
	});
</script>

{@render children()}

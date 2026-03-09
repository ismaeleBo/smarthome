<script lang="ts">
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { session } from '$lib/stores/session.svelte';

	onMount(async () => {
		await session.bootstrap();

		if (session.state.user) {
			await goto(session.state.user.role === 'ADMIN' ? '/app/admin' : '/app/home');
		} else {
			await goto('/login');
		}
	});
</script>

<p class="p-6 text-sm opacity-70">Redirect...</p>

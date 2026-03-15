<script lang="ts">
	import { session } from '$lib/stores/session.svelte';
	import { page } from '$app/state';

	const s = session.state;

	function getHomePath() {
		if (s.user?.role === 'ADMIN') return '/app/admin';
		if (s.user?.role === 'ANALYST') return '/app/analyst';
		return '/app/home';
	}
</script>

<header class="flex flex-col gap-4 border-b bg-white p-6 sm:flex-row sm:items-center sm:justify-between">
	<div>
		<a href={getHomePath()} class="text-xl font-bold hover:opacity-80">SmartHome</a>
		<p class="text-xs opacity-60">
			{s.user?.firstName} {s.user?.lastName} ({s.user?.role}) - {s.user?.email}
		</p>
	</div>

	<div class="flex flex-wrap items-center gap-3">
		{#if (s.user?.role === 'HEAD' || s.user?.role === 'MEMBER') && s.homes.length > 0}
			<div class="flex items-center gap-2">
				<select
					id="header-home"
					class="rounded-xl border bg-gray-50 p-2 text-sm"
					value={s.selectedHomeId}
					onchange={(e) => session.selectHome(Number((e.currentTarget as HTMLSelectElement).value))}
				>
					{#each s.homes as h}
						<option value={h.homeId}>Home #{h.homeId} - {h.city}</option>
					{/each}
				</select>
			</div>
		{/if}

		{#if s.user?.role === 'HEAD'}
			<a
				href="/app/head/invitations"
				class={`rounded-xl border px-3 py-2 text-sm hover:bg-black hover:text-white ${page.url.pathname.includes('/invitations') ? 'bg-black text-white' : ''}`}
			>
				Manage invitations
			</a>
		{/if}

		<button
			class="rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm font-medium text-red-600 hover:bg-red-100"
			onclick={() => session.logout()}
		>
			Logout
		</button>
	</div>
</header>

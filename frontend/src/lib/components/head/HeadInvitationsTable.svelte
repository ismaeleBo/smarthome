<script lang="ts">
	import type { InvitationResponse } from '$lib/contracts/home';

	type Props = {
		loading: boolean;
		error: string | null;
		invitations: InvitationResponse[];
		revokingToken: string | null;
		onRevoke: (token: string) => void;
	};

	let { loading, error, invitations, revokingToken, onRevoke }: Props = $props();

	function formatDateTime(value: string): string {
		const date = new Date(value);
		if (Number.isNaN(date.getTime())) return value;

		return new Intl.DateTimeFormat('it-IT', {
			dateStyle: 'short',
			timeStyle: 'short'
		}).format(date);
	}

	function statusBadgeClass(status: string): string {
		switch (status) {
			case 'VALID':
				return 'bg-green-100 text-green-800 border-green-200';
			case 'CONSUMED':
				return 'bg-blue-100 text-blue-800 border-blue-200';
			case 'EXPIRED':
				return 'bg-amber-100 text-amber-800 border-amber-200';
			case 'REVOKED':
				return 'bg-red-100 text-red-800 border-red-200';
			default:
				return 'bg-gray-100 text-gray-800 border-gray-200';
		}
	}

	function revoke(token: string) {
		onRevoke(token);
	}
</script>

<section class="space-y-4 rounded-2xl border bg-white p-5">
	<div>
		<h2 class="text-lg font-semibold">Selected Home Invitations</h2>
		<p class="text-sm opacity-70">List of invitations created for the current home.</p>
	</div>

	{#if error}
		<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
			{error}
		</div>
	{/if}

	{#if loading}
		<p class="text-sm opacity-70">Loading invitations...</p>
	{:else if invitations.length === 0}
		<p class="text-sm opacity-70">No invitations available for this home.</p>
	{:else}
		<div class="overflow-x-auto">
			<table class="w-full text-sm">
				<thead>
					<tr class="border-b text-left">
						<th class="py-2 pr-3">Email</th>
						<th class="py-2 pr-3">Expiration</th>
						<th class="py-2 pr-3">Status</th>
						<th class="py-2 pr-3">Actions</th>
					</tr>
				</thead>
				<tbody>
					{#each invitations as invitation}
						<tr class="border-b align-top">
							<td class="py-3 pr-3">{invitation.email}</td>
							<td class="py-3 pr-3">{formatDateTime(invitation.expiresAt)}</td>
							<td class="py-3 pr-3">
								<span
									class={`inline-flex rounded-full border px-2 py-1 text-xs ${statusBadgeClass(invitation.status)}`}
								>
									{invitation.status}
								</span>
							</td>
							<td class="py-3 pr-3">
								{#if invitation.status === 'VALID'}
									<button
										class="rounded-xl border px-3 py-2 text-xs disabled:opacity-50"
										onclick={() => revoke(invitation.token)}
										disabled={revokingToken === invitation.token}
									>
										{#if revokingToken === invitation.token}
											Revoking...
										{:else}
											Revoke
										{/if}
									</button>
								{:else}
									<span class="text-xs opacity-50">-</span>
								{/if}
							</td>
						</tr>
					{/each}
				</tbody>
			</table>
		</div>
	{/if}
</section>

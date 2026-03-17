<script lang="ts">
	import { onMount } from 'svelte';
	import { session } from '$lib/stores/session.svelte';
	import { HomeApi } from '$lib/api/home';
	import { AuthApi } from '$lib/api/auth';
	import { MeasurementsApi } from '$lib/api/measurements';
	import { HttpError } from '$lib/api/http';
	import { toast } from 'svelte-sonner';

	import type { HomeResponse } from '$lib/contracts/home';
	import type { Role, UserSummaryResponse } from '$lib/contracts/auth';

	let loading = $state(true);

	let configuredHomes: HomeResponse[] = $state([]);
	let datasetHomeIds: number[] = [];
	let users: UserSummaryResponse[] = $state([]);
	let heads: UserSummaryResponse[] = $state([]);
	let analysts: UserSummaryResponse[] = $state([]);

	let selectedNewHomeId: number | null = $state(null);
	let configureForm = $state({
		address: '',
		streetNumber: '',
		city: '',
		pricePerKwh: ''
	});

	let createUserForm = $state({
		role: 'HEAD' as Role,
		email: '',
		firstName: '',
		lastName: '',
		birthDate: ''
	});

	let assignHeadSelections: Record<number, string> = $state({});
	let analystAssignment = $state({
		analystUserId: '',
		homeIds: [] as number[]
	});

	let analystAssignmentsLoading = $state(false);

	async function loadAnalystAssignedHomes(analystUserId: string) {
		if (!analystUserId) {
			analystAssignment.homeIds = [];
			return;
		}

		analystAssignmentsLoading = true;

		try {
			const token = tokenOrThrow();
			const assignedHomeIds = await HomeApi.getAnalystAssignedHomes(token, analystUserId);
			analystAssignment.homeIds = assignedHomeIds;
		} catch (error) {
			toast.error(parseError(error, 'Error load homes assigned to the analyst'));
			analystAssignment.homeIds = [];
		} finally {
			analystAssignmentsLoading = false;
		}
	}

	async function handleAnalystChange(analystUserId: string) {
		analystAssignment.analystUserId = analystUserId;
		await loadAnalystAssignedHomes(analystUserId);
	}

	function tokenOrThrow(): string {
		const token = session.state.token;
		if (!token) throw new Error('Missing token');
		return token;
	}

	function parseError(error: unknown, fallback = 'Operazione non riuscita'): string {
		if (error instanceof HttpError) return error.apiError?.message ?? error.message;
		if (error instanceof Error) return error.message;
		return fallback;
	}

	function availableHomeIds(): number[] {
		const configuredIds = new Set(configuredHomes.map((h) => h.homeId));
		return datasetHomeIds.filter((id) => !configuredIds.has(id));
	}

	function homeStatusLabel(homeId: number): string {
		return configuredHomes.find((h) => h.homeId === homeId)?.status ?? '-';
	}

	function toggleAnalystHome(homeId: number, checked: boolean) {
		if (checked) {
			if (!analystAssignment.homeIds.includes(homeId)) {
				analystAssignment.homeIds = [...analystAssignment.homeIds, homeId];
			}
			return;
		}

		analystAssignment.homeIds = analystAssignment.homeIds.filter((id) => id !== homeId);
	}

	async function loadAdminData() {
		loading = true;

		try {
			const token = tokenOrThrow();

			const [homesRes, datasetIdsRes, usersRes, headsRes, analystsRes] = await Promise.all([
				HomeApi.allHomes(token),
				MeasurementsApi.homes(token),
				AuthApi.adminUsers(token),
				AuthApi.adminUsersByRole(token, 'HEAD'),
				AuthApi.adminUsersByRole(token, 'ANALYST')
			]);

			configuredHomes = homesRes.sort((a, b) => a.homeId - b.homeId);
			datasetHomeIds = [...datasetIdsRes].sort((a, b) => a - b);
			users = usersRes;
			heads = headsRes;
			analysts = analystsRes;

			const available = availableHomeIds();
			if (
				available.length > 0 &&
				(selectedNewHomeId == null || !available.includes(selectedNewHomeId))
			) {
				selectedNewHomeId = available[0];
			}
			if (available.length === 0) {
				selectedNewHomeId = null;
			}
		} catch (error) {
			toast.error(parseError(error, 'Impossibile caricare i dati amministrativi'));
		} finally {
			loading = false;
			const analystStillExists =
				analystAssignment.analystUserId &&
				analysts.some((a) => a.id === analystAssignment.analystUserId);

			if (!analystStillExists) {
				analystAssignment = {
					analystUserId: '',
					homeIds: []
				};
			}
		}
	}

	async function submitConfigureHome() {
		if (selectedNewHomeId == null) {
			toast.error('Select a home to configure');
			return;
		}

		const price = Number(configureForm.pricePerKwh);
		if (!Number.isFinite(price)) {
			toast.error('The price per kWh must be a valid number');
			return;
		}

		try {
			const token = tokenOrThrow();

			await HomeApi.configureHome(token, selectedNewHomeId, {
				address: configureForm.address.trim(),
				streetNumber: configureForm.streetNumber.trim(),
				city: configureForm.city.trim(),
				pricePerKwh: price
			});

			toast.success(`Home #${selectedNewHomeId} configured correctly`);

			configureForm = {
				address: '',
				streetNumber: '',
				city: '',
				pricePerKwh: ''
			};

			await loadAdminData();
		} catch (error) {
			toast.error(parseError(error, 'Home configuration failed'));
		}
	}

	async function submitCreateUser() {
		if (createUserForm.role !== 'HEAD' && createUserForm.role !== 'ANALYST') {
			toast.error('The admin can only create HEAD or ANALYST accounts');
			return;
		}

		try {
			const token = tokenOrThrow();

			const res = await AuthApi.provisionUser(token, {
				email: createUserForm.email.trim(),
				role: createUserForm.role,
				firstName: createUserForm.firstName.trim(),
				lastName: createUserForm.lastName.trim(),
				birthDate: createUserForm.birthDate
			});

			toast.success(`Utente creato correttamente (id: ${res.userId})`);

			createUserForm = {
				role: 'HEAD',
				email: '',
				firstName: '',
				lastName: '',
				birthDate: ''
			};

			await loadAdminData();
		} catch (error) {
			toast.error(parseError(error, 'User creation failed'));
		}
	}

	async function submitAssignHead(homeId: number) {
		const headUserId = assignHeadSelections[homeId];
		if (!headUserId) {
			toast.error(`Select a Head for home #${homeId}`);
			return;
		}

		try {
			const token = tokenOrThrow();

			await HomeApi.assignHead(token, homeId, { headUserId });
			toast.success(`Head assigned to home #${homeId}`);
			await loadAdminData();
		} catch (error) {
			toast.error(parseError(error, 'Head assignment failed'));
		}
	}

	async function submitAssignAnalystHomes() {
		if (!analystAssignment.analystUserId) {
			toast.error('Seleziona un analyst');
			return;
		}

		try {
			const token = tokenOrThrow();

			await HomeApi.assignAnalystHomes(token, {
				analystUserId: analystAssignment.analystUserId,
				homeIds: analystAssignment.homeIds
			});

			toast.success('Assegnazioni analyst aggiornate correttamente');
		} catch (error) {
			toast.error(parseError(error, 'Aggiornamento assegnazioni analyst non riuscito'));
		}
	}

	async function disableHome(homeId: number) {
		try {
			const token = tokenOrThrow();
			await HomeApi.disableHome(token, homeId);
			toast.success(`Home #${homeId} disabled`);
			await loadAdminData();
		} catch (error) {
			toast.error(parseError(error, 'Failed to disable home'));
		}
	}

	async function reactivateHome(homeId: number) {
		try {
			const token = tokenOrThrow();
			await HomeApi.reactivateHome(token, homeId);
			toast.success(`Home #${homeId} reactivated`);
			await loadAdminData();
		} catch (error) {
			toast.error(parseError(error, 'Failed to reactivate home'));
		}
	}

	onMount(loadAdminData);
</script>

<div class="space-y-6 p-6">
	{#if loading}
		<div class="rounded-2xl border bg-white p-4 text-sm opacity-70">
			Loading administrative data...
		</div>
	{:else}
		<div class="grid gap-6 lg:grid-cols-2">
			<!-- CONFIGURE HOME -->
			<section class="space-y-4 rounded-2xl border bg-white p-5">
				<div>
					<h2 class="text-lg font-semibold">Configure new Home</h2>
				</div>

				<div class="grid gap-3">
					<div>
						<label for="homeId" class="text-sm font-medium">Home ID</label>
						<select
							id="homeId"
							name="homeId"
							class="mt-1 w-full rounded-xl border p-2"
							bind:value={selectedNewHomeId}
							disabled={availableHomeIds().length === 0}
						>
							{#if availableHomeIds().length === 0}
								<option value={null}>No configurable homes available</option>
							{:else}
								{#each availableHomeIds() as id}
									<option value={id}>{id}</option>
								{/each}
							{/if}
						</select>
					</div>

					<div>
						<label for="address" class="text-sm font-medium">Address</label>
						<input
							name="address"
							id="address"
							class="mt-1 w-full rounded-xl border p-2"
							bind:value={configureForm.address}
						/>
					</div>

					<div class="grid gap-3 sm:grid-cols-2">
						<div>
							<label for="streetNumber" class="text-sm font-medium">Street Number</label>
							<input
								id="streetNumber"
								class="mt-1 w-full rounded-xl border p-2"
								bind:value={configureForm.streetNumber}
							/>
						</div>

						<div>
							<label for="city" class="text-sm font-medium">City</label>
							<input
								id="city"
								class="mt-1 w-full rounded-xl border p-2"
								bind:value={configureForm.city}
							/>
						</div>
					</div>

					<div>
						<label for="pricePerKwh" class="text-sm font-medium">Price per kWh</label>
						<input
							id="pricePerKwh"
							class="mt-1 w-full rounded-xl border p-2"
							type="number"
							min="0"
							step="0.01"
							bind:value={configureForm.pricePerKwh}
						/>
					</div>

					<button class="rounded-xl bg-black p-2 text-white" onclick={submitConfigureHome}>
						Configure Home
					</button>
				</div>
			</section>

			<!-- CREA UTENTE -->
			<section class="space-y-4 rounded-2xl border bg-white p-5">
				<div>
					<h2 class="text-lg font-semibold">Create User</h2>
					<p class="text-sm opacity-70">
						The user is created inactive and will complete activation via token.
					</p>
				</div>

				<div class="grid gap-3">
					<div>
						<label for="role" class="text-sm font-medium">Role</label>
						<select
							id="role"
							class="mt-1 w-full rounded-xl border p-2"
							bind:value={createUserForm.role}
						>
							<option value="HEAD">HEAD</option>
							<option value="ANALYST">ANALYST</option>
						</select>
					</div>

					<div>
						<label for="email" class="text-sm font-medium">Email</label>
						<input
							id="email"
							class="mt-1 w-full rounded-xl border p-2"
							type="email"
							bind:value={createUserForm.email}
						/>
					</div>

					<div class="grid gap-3 sm:grid-cols-2">
						<div>
							<label for="firstName" class="text-sm font-medium">First name</label>
							<input
								id="firstName"
								class="mt-1 w-full rounded-xl border p-2"
								bind:value={createUserForm.firstName}
							/>
						</div>
						<div>
							<label for="lastName" class="text-sm font-medium">Last name</label>
							<input
								id="lastName"
								class="mt-1 w-full rounded-xl border p-2"
								bind:value={createUserForm.lastName}
							/>
						</div>
					</div>

					<div>
						<label for="birthDate" class="text-sm font-medium">Birth date</label>
						<input
							id="birthDate"
							class="mt-1 w-full rounded-xl border p-2"
							type="date"
							bind:value={createUserForm.birthDate}
						/>
					</div>

					<button class="rounded-xl bg-black p-2 text-white" onclick={submitCreateUser}>
						Create User
					</button>
				</div>
			</section>
		</div>

		<!-- ASSIGN ANALYST -->
		<section class="space-y-4 rounded-2xl border bg-white p-5">
			<div>
				<h2 class="text-lg font-semibold">Assign Home to Analyst</h2>
				<p class="text-sm opacity-70">Select an analyst and the configured homes to associate.</p>
			</div>

			<div class="space-y-3">
				<div>
					<label for="analyst" class="text-sm font-medium">Analyst</label>
					<select
						name="analyst"
						id="analyst"
						class="mt-1 w-full rounded-xl border p-2"
						bind:value={analystAssignment.analystUserId}
						onchange={(e) => handleAnalystChange((e.currentTarget as HTMLSelectElement).value)}
					>
						<option value="">Seleziona Analyst</option>
						{#each analysts as analyst}
							<option value={analyst.id}>
								{analyst.firstName}
								{analyst.lastName} - {analyst.email}
							</option>
						{/each}
					</select>
				</div>

				{#if analystAssignment.analystUserId}
					<div class="space-y-2">
						<p class="text-sm font-medium">Available Homes</p>

						{#if analystAssignmentsLoading}
							<div class="rounded-xl border p-3 text-sm opacity-70">
								Loading homes assignments...
							</div>
						{:else}
							<div class="max-h-56 space-y-2 overflow-auto rounded-xl border p-3">
								{#each configuredHomes as home}
									<label class="flex items-center gap-2 text-sm">
										<input
											type="checkbox"
											checked={analystAssignment.homeIds.includes(home.homeId)}
											onchange={(e) =>
												toggleAnalystHome(
													home.homeId,
													(e.currentTarget as HTMLInputElement).checked
												)}
											disabled={!analystAssignment.analystUserId}
										/>
										<span>Home #{home.homeId} - {homeStatusLabel(home.homeId)}</span>
									</label>
								{/each}
							</div>
						{/if}
					</div>

					<button
						class="rounded-xl bg-black p-2 text-white disabled:opacity-50"
						onclick={submitAssignAnalystHomes}
						disabled={!analystAssignment.analystUserId || analystAssignmentsLoading}
					>
						Save Assignments
					</button>
				{/if}
			</div>
		</section>

		<div class="grid gap-6 xl:grid-cols-[2fr,1fr]">
			<section class="space-y-4 rounded-2xl border bg-white p-5">
				<div>
					<h2 class="text-lg font-semibold">Configured Homes</h2>
					<p class="text-sm opacity-70">List of currently configured homes.</p>
				</div>

				<div class="max-h-96 overflow-x-auto overflow-y-scroll">
					<table class="w-full text-sm">
						<thead>
							<tr class="border-b text-left">
								<th class="py-2 pr-3">Home</th>
								<th class="py-2 pr-3">Status</th>
								<th class="py-2 pr-3">Address</th>
								<th class="py-2 pr-3">Price/kWh</th>
								<th class="py-2 pr-3">Head</th>
								<th class="py-2 pr-3">Actions</th>
							</tr>
						</thead>
						<tbody>
							{#each configuredHomes as home}
								<tr class="border-b align-top">
									<td class="py-3 pr-3 font-medium">#{home.homeId}</td>
									<td class="py-3 pr-3">{home.status}</td>
									<td class="py-3 pr-3">{home.address} {home.streetNumber}, {home.city}</td>
									<td class="py-3 pr-3">{home.pricePerKwh}€</td>
									<td class="py-3 pr-3">
										<div class="space-y-2">
											{#if home.headUserId}
												<div class="text-xs opacity-70">
													{users.find((u) => u.id === home.headUserId)?.firstName +
														' ' +
														users.find((u) => u.id === home.headUserId)?.lastName}
												</div>
											{:else}
												<div class="text-xs opacity-70">No assigned Head</div>
												<select
													class="w-full rounded-xl border p-2"
													bind:value={assignHeadSelections[home.homeId]}
												>
													<option value="">Select Head</option>
													{#each heads as head}
														<option value={head.id}>
															{head.firstName}
															{head.lastName} - {head.email}
														</option>
													{/each}
												</select>
												<button
													class="rounded-xl border px-3 py-2 text-xs"
													onclick={() => submitAssignHead(home.homeId)}
												>
													Assign Head
												</button>
											{/if}
										</div>
									</td>
									<td class="py-3 pr-3">
										<div class="flex flex-col gap-2">
											{#if home.status === 'DISABLED'}
												<button
													class="rounded-xl border px-3 py-2 text-xs"
													onclick={() => reactivateHome(home.homeId)}
												>
													Reactivate
												</button>
											{:else}
												<button
													class="rounded-xl border px-3 py-2 text-xs"
													onclick={() => disableHome(home.homeId)}
												>
													Disable
												</button>
											{/if}
										</div>
									</td>
								</tr>
							{/each}
						</tbody>
					</table>
				</div>
			</section>
		</div>

		<section class="space-y-4 rounded-2xl border bg-white p-5">
			<div>
				<h2 class="text-lg font-semibold">Users</h2>
				<p class="text-sm opacity-70">Quick view of users present in the system.</p>
			</div>

			<div class="max-h-96 overflow-x-auto overflow-y-scroll">
				<table class="w-full text-sm">
					<thead>
						<tr class="border-b text-left">
							<th class="py-2 pr-3">Name</th>
							<th class="py-2 pr-3">Email</th>
							<th class="py-2 pr-3">Role</th>
							<th class="py-2 pr-3">Status</th>
							<th class="py-2 pr-3">Date of Birth</th>
						</tr>
					</thead>
					<tbody>
						{#each users as user}
							<tr class="border-b">
								<td class="py-3 pr-3">{user.firstName} {user.lastName}</td>
								<td class="py-3 pr-3">{user.email}</td>
								<td class="py-3 pr-3">{user.role}</td>
								<td class="py-3 pr-3">{user.status}</td>
								<td class="py-3 pr-3">{user.dateOfBirth}</td>
							</tr>
						{/each}
					</tbody>
				</table>
			</div>
		</section>
	{/if}
</div>

<script lang="ts">
	export interface RegisterMemberFormData {
		email: string;
		firstName: string;
		lastName: string;
		dateOfBirth: string;
		password: string;
		confirmPassword: string;
	}

	type Props = {
		loading: boolean;
		error: string | null;
		success: string | null;
		email: string;
		firstName: string;
		lastName: string;
		dateOfBirth: string;
		password: string;
		confirmPassword: string;
		onSubmit: (data: RegisterMemberFormData) => void;
	};

	let {
		loading,
		error,
		success,
		email = $bindable(),
		firstName = $bindable(),
		lastName = $bindable(),
		dateOfBirth = $bindable(),
		password = $bindable(),
		confirmPassword = $bindable(),
		onSubmit
	}: Props = $props();

	function handleSubmit() {
		onSubmit({ email, firstName, lastName, dateOfBirth, password, confirmPassword });
	}
</script>

<section class="space-y-4 rounded-2xl border bg-white p-6">
	<div>
		<h1 class="text-xl font-semibold">Complete Member Registration</h1>
		<p class="text-sm opacity-70">
			Enter your details to create your account and accept the invitation.
		</p>
	</div>

	{#if error}
		<div class="rounded-xl border border-red-200 bg-red-50 p-3 text-sm">
			{error}
		</div>
	{/if}

	{#if success}
		<div class="rounded-xl border border-green-200 bg-green-50 p-3 text-sm">
			{success}
		</div>
	{/if}

	<div class="grid gap-3">
		<div>
			<label for="email" class="text-sm font-medium">Email</label>
			<input
				name="email"
				id="email"
				class="mt-1 w-full rounded-xl border p-2"
				type="email"
				bind:value={email}
			/>
		</div>

		<div class="grid gap-3 sm:grid-cols-2">
			<div>
				<label for="firstName" class="text-sm font-medium">Nome</label>
				<input
					name="firstName"
					id="firstName"
					class="mt-1 w-full rounded-xl border p-2"
					bind:value={firstName}
				/>
			</div>

			<div>
				<label for="lastName" class="text-sm font-medium">Cognome</label>
				<input
					name="lastName"
					id="lastName"
					class="mt-1 w-full rounded-xl border p-2"
					bind:value={lastName}
				/>
			</div>
		</div>

		<div>
			<label for="dateOfBirth" class="text-sm font-medium">Birth Date</label>
			<input
				name="dateOfBirth"
				id="dateOfBirth"
				class="mt-1 w-full rounded-xl border p-2"
				type="date"
				bind:value={dateOfBirth}
			/>
		</div>

		<div>
			<label for="password" class="text-sm font-medium">Password</label>
			<input
				name="password"
				id="password"
				class="mt-1 w-full rounded-xl border p-2"
				type="password"
				bind:value={password}
			/>
		</div>

		<div>
			<label for="confirmPassword" class="text-sm font-medium">Confirm Password</label>
			<input
				name="confirmPassword"
				id="confirmPassword"
				class="mt-1 w-full rounded-xl border p-2"
				type="password"
				bind:value={confirmPassword}
			/>
		</div>

		<button
			class="rounded-xl bg-black px-4 py-2 text-white disabled:opacity-50"
			onclick={handleSubmit}
			disabled={loading}
		>
			{loading ? 'Registering...' : 'Register'}
		</button>
	</div>
</section>

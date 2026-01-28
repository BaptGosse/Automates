<script lang="ts">
	import { onMount } from 'svelte';
	import { sessionId } from '../stores/automaton';
	import { api } from '../api/client';
	import type { AutomatonInfo } from '../types/automaton';

	let info: AutomatonInfo | null = null;
	let loading = false;
	let error: string | null = null;

	export async function refresh() {
		if (!$sessionId) return;

		loading = true;
		error = null;

		try {
			info = await api.getAutomatonInfo($sessionId);
		} catch (e) {
			error = 'Erreur lors du chargement des informations';
			console.error(e);
		} finally {
			loading = false;
		}
	}

	onMount(() => {
		refresh();
	});
</script>

<div class="info-panel">
	<h3 class="text-lg font-bold mb-3">Informations de l'Automate</h3>

	{#if loading}
		<p class="text-gray-500">Chargement...</p>
	{:else if error}
		<p class="text-red-500">{error}</p>
	{:else if info}
		<div class="info-grid">
			<div class="info-row">
				<span class="info-label">Nom :</span>
				<span class="info-value">{info.name}</span>
			</div>

			<div class="info-row">
				<span class="info-label">Type :</span>
				<span class="info-value">{info.type}</span>
			</div>

			<div class="separator"></div>

			<div class="info-row">
				<span class="info-label">Alphabet Σ :</span>
				<span class="info-value">
					{#if info.alphabet.length === 0}
						∅ (vide)
					{:else}
						{'{'}{info.alphabet.join(', ')}{'}'}
					{/if}
				</span>
			</div>

			<div class="info-row">
				<span class="info-label">Nombre d'états :</span>
				<span class="info-value">{info.statesCount}</span>
			</div>

			<div class="info-row">
				<span class="info-label">Nombre de transitions :</span>
				<span class="info-value">{info.transitionsCount}</span>
			</div>

			<div class="separator"></div>

			<div class="info-row">
				<span class="info-label">État initial :</span>
				<span class="info-value">{info.initialState || 'Aucun'}</span>
			</div>

			<div class="info-row">
				<span class="info-label">États acceptants :</span>
				<span class="info-value">
					{#if info.acceptingStates.length === 0}
						Aucun
					{:else}
						{'{'}{info.acceptingStates.join(', ')}{'}'}
					{/if}
				</span>
			</div>

			<div class="separator"></div>

			<div class="info-row">
				<span class="info-label">Déterministe :</span>
				<span class="info-value" class:text-green-600={info.isDeterministic} class:text-red-600={!info.isDeterministic}>
					{info.isDeterministic ? '✓ Oui' : '✗ Non'}
				</span>
			</div>

			<div class="info-row">
				<span class="info-label">Complet :</span>
				<span class="info-value" class:text-green-600={info.isComplete} class:text-red-600={!info.isComplete}>
					{info.isComplete ? '✓ Oui' : '✗ Non'}
				</span>
			</div>

			<div class="separator"></div>

			<div class="info-row">
				<span class="info-label">Expression régulière :</span>
				<span class="info-value break-words">{info.regex}</span>
			</div>

			<div class="info-row">
				<span class="info-label">Description :</span>
				<span class="info-value break-words text-sm">{info.languageDescription}</span>
			</div>
		</div>
	{:else}
		<p class="text-gray-500 italic">Aucune information disponible</p>
	{/if}
</div>

<style>
	.info-panel {
		padding: 1rem;
		background-color: #f9f9f9;
		border: 1px solid #cccccc;
		border-radius: 4px;
		overflow-y: auto;
		max-height: 600px;
	}

	.info-grid {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}

	.info-row {
		display: grid;
		grid-template-columns: 180px 1fr;
		gap: 1rem;
		padding: 0.25rem 0;
	}

	.info-label {
		font-weight: bold;
		font-size: 0.875rem;
	}

	.info-value {
		font-size: 0.875rem;
		word-wrap: break-word;
	}

	.separator {
		height: 1px;
		background-color: #ddd;
		margin: 0.5rem 0;
	}

	.text-green-600 {
		color: #16a34a;
	}

	.text-red-600 {
		color: #dc2626;
	}

	.break-words {
		word-break: break-word;
	}
</style>

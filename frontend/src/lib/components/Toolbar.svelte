<script lang="ts">
	import { currentTool, selectedState, automaton } from '../stores/automaton';

	export let onClear: (() => void) | undefined = undefined;
	export let onSetInitial: (() => void) | undefined = undefined;
	export let onSetAccepting: (() => void) | undefined = undefined;
	export let onDelete: (() => void) | undefined = undefined;

	function setTool(tool: 'select' | 'addState' | 'addTransition') {
		$currentTool = tool;
	}

	function handleClear() {
		if (confirm('Voulez-vous vraiment effacer tout l\'automate ?')) {
			automaton.reset();
			onClear?.();
		}
	}

	function handleSetInitial() {
		if ($selectedState) {
			onSetInitial?.();
		}
	}

	function handleSetAccepting() {
		if ($selectedState) {
			onSetAccepting?.();
		}
	}

	function handleDelete() {
		if ($selectedState) {
			onDelete?.();
		}
	}
</script>

<div class="toolbar">
	<!-- Outils d'édition -->
	<div class="tool-group">
		<button
			class="tool-button"
			class:active={$currentTool === 'select'}
			on:click={() => setTool('select')}
			title="Outil Sélection"
		>
			<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
				<path d="M3 3l7.07 16.97 2.51-7.39 7.39-2.51L3 3z" />
			</svg>
			<span>Sélection</span>
		</button>

		<button
			class="tool-button"
			class:active={$currentTool === 'addState'}
			on:click={() => setTool('addState')}
			title="Ajouter État"
		>
			<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
				<circle cx="12" cy="12" r="10" />
				<line x1="12" y1="8" x2="12" y2="16" />
				<line x1="8" y1="12" x2="16" y2="12" />
			</svg>
			<span>Ajouter État</span>
		</button>

		<button
			class="tool-button"
			class:active={$currentTool === 'addTransition'}
			on:click={() => setTool('addTransition')}
			title="Ajouter Transition"
		>
			<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
				<line x1="5" y1="12" x2="19" y2="12" />
				<polyline points="15 8 19 12 15 16" />
			</svg>
			<span>Ajouter Transition</span>
		</button>
	</div>

	<div class="separator"></div>

	<!-- Actions sur états -->
	<div class="tool-group">
		<button
			class="tool-button"
			on:click={handleSetInitial}
			disabled={!$selectedState}
			title="Définir comme état initial"
		>
			<span>Définir Initial</span>
		</button>

		<button
			class="tool-button"
			on:click={handleSetAccepting}
			disabled={!$selectedState}
			title="Basculer état acceptant"
		>
			<span>Définir Acceptant</span>
		</button>

		<button
			class="tool-button delete-button"
			on:click={handleDelete}
			disabled={!$selectedState}
			title="Supprimer l'élément sélectionné"
		>
			<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
				<polyline points="3 6 5 6 21 6" />
				<path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
			</svg>
			<span>Supprimer</span>
		</button>
	</div>

	<div class="separator"></div>

	<!-- Actions globales -->
	<div class="tool-group">
		<button class="tool-button clear-button" on:click={handleClear} title="Effacer tout">
			<span>Effacer Tout</span>
		</button>
	</div>
</div>

<style>
	.toolbar {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		padding: 0.75rem;
		background-color: #f3f4f6;
		border-bottom: 1px solid #d1d5db;
	}

	.tool-group {
		display: flex;
		gap: 0.25rem;
	}

	.separator {
		width: 1px;
		height: 32px;
		background-color: #d1d5db;
	}

	.tool-button {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		padding: 0.5rem 1rem;
		background-color: white;
		border: 1px solid #d1d5db;
		border-radius: 4px;
		font-size: 0.875rem;
		cursor: pointer;
		transition: all 0.2s;
	}

	.tool-button:hover:not(:disabled) {
		background-color: #e5e7eb;
	}

	.tool-button.active {
		background-color: #3b82f6;
		color: white;
		border-color: #2563eb;
	}

	.tool-button:disabled {
		opacity: 0.5;
		cursor: not-allowed;
	}

	.delete-button:hover:not(:disabled) {
		background-color: #fef2f2;
		border-color: #dc2626;
		color: #dc2626;
	}

	.clear-button:hover {
		background-color: #fff7ed;
		border-color: #ea580c;
		color: #ea580c;
	}
</style>

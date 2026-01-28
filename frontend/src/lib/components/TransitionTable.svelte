<script lang="ts">
	import { automaton } from '../stores/automaton';
	import type { State } from '../types/automaton';

	/**
	 * Génère la table de transitions
	 */
	function getTransitionTable() {
		const table = new Map<string, Map<string, Set<string>>>();

		// Initialiser la table
		$automaton.states.forEach((state) => {
			const symbolMap = new Map<string, Set<string>>();
			$automaton.alphabet.forEach((symbol) => {
				symbolMap.set(symbol, new Set<string>());
			});
			table.set(state.id, symbolMap);
		});

		// Remplir la table
		$automaton.transitions.forEach((transition) => {
			if (!transition.symbol || transition.symbol === 'ε') return;

			const symbolMap = table.get(transition.from.id);
			if (symbolMap) {
				const targetStates = symbolMap.get(transition.symbol);
				if (targetStates) {
					targetStates.add(transition.to.label);
				}
			}
		});

		return table;
	}

	/**
	 * Formatte l'ensemble des états cibles
	 */
	function formatTargets(targets: Set<string>): string {
		if (targets.size === 0) return '—';
		if (targets.size === 1) return Array.from(targets)[0];
		return `{${Array.from(targets).sort().join(', ')}}`;
	}

	/**
	 * Obtient le label d'un état avec ses marqueurs
	 */
	function getStateLabel(state: State): string {
		let label = '';
		if (state.initial) label += '→ ';
		label += state.label;
		if (state.accepting) label += ' *';
		return label;
	}

	$: transitionTable = getTransitionTable();
</script>

<div class="transition-table-container">
	<h3 class="text-lg font-bold mb-3">Table de Transitions</h3>

	{#if $automaton.states.length === 0}
		<p class="text-gray-500 italic">Aucun état défini</p>
	{:else if $automaton.alphabet.length === 0}
		<p class="text-gray-500 italic">Aucun symbole dans l'alphabet</p>
	{:else}
		<div class="overflow-auto">
			<table class="transition-table">
				<thead>
					<tr>
						<th class="header-cell">δ</th>
						{#each $automaton.alphabet as symbol}
							<th class="header-cell">{symbol}</th>
						{/each}
					</tr>
				</thead>
				<tbody>
					{#each $automaton.states as state}
						<tr>
							<td class="state-cell font-semibold">{getStateLabel(state)}</td>
							{#each $automaton.alphabet as symbol}
								{@const targets = transitionTable.get(state.id)?.get(symbol) || new Set()}
								<td class="data-cell">{formatTargets(targets)}</td>
							{/each}
						</tr>
					{/each}
				</tbody>
			</table>
		</div>

		<div class="mt-3 text-sm text-gray-600">
			<p>Σ = {'{'}{$automaton.alphabet.join(', ')}{'}'}</p>
			<p>
				{$automaton.states.length} état{$automaton.states.length > 1 ? 's' : ''},
				{$automaton.transitions.length} transition{$automaton.transitions.length > 1 ? 's' : ''}
			</p>
		</div>

		<div class="mt-3 text-xs text-gray-500">
			<p>Légende : → état initial | état * acceptant</p>
		</div>
	{/if}
</div>

<style>
	.transition-table-container {
		padding: 1rem;
		background-color: #f9f9f9;
		border: 1px solid #cccccc;
		border-radius: 4px;
	}

	.transition-table {
		width: 100%;
		border-collapse: collapse;
		background-color: white;
	}

	.header-cell {
		padding: 0.5rem;
		border: 1px solid #ddd;
		background-color: #f0f0f0;
		font-weight: bold;
		text-align: center;
	}

	.state-cell {
		padding: 0.5rem;
		border: 1px solid #ddd;
		background-color: #f9f9f9;
		text-align: center;
	}

	.data-cell {
		padding: 0.5rem;
		border: 1px solid #ddd;
		text-align: center;
	}
</style>

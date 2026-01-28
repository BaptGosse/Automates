<script lang="ts">
	import { automaton, selectedState, selectedTransition, currentTool } from '../stores/automaton';
	import type { State, Transition } from '../types/automaton';
	import { STATE_RADIUS } from '../types/automaton';

	export let onStateAdded: ((state: State) => void) | undefined = undefined;
	export let onTransitionAdded: ((transition: Transition) => void) | undefined = undefined;
	export let onStateSelected: ((state: State | null) => void) | undefined = undefined;

	let transitionStartState: State | null = null;

	/**
	 * Gère le clic sur le canvas
	 */
	function handleCanvasClick(event: MouseEvent) {
		const svg = event.currentTarget as SVGSVGElement;
		const rect = svg.getBoundingClientRect();
		const x = event.clientX - rect.left;
		const y = event.clientY - rect.top;

		// Vérifier si on a cliqué sur un état
		const clickedState = findStateAt(x, y);

		if ($currentTool === 'select') {
			if (clickedState) {
				$selectedState = clickedState;
				$selectedTransition = null;
				onStateSelected?.(clickedState);
			} else {
				$selectedState = null;
				$selectedTransition = null;
				onStateSelected?.(null);
			}
		} else if ($currentTool === 'addState') {
			if (!clickedState) {
				// Créer un nouvel état
				const newState: State = {
					id: crypto.randomUUID(),
					label: generateStateLabel(),
					x,
					y,
					initial: false,
					accepting: false
				};
				automaton.addState(newState);
				onStateAdded?.(newState);
			}
		} else if ($currentTool === 'addTransition') {
			if (clickedState) {
				if (transitionStartState === null) {
					// Premier clic : sélectionner l'état de départ
					transitionStartState = clickedState;
				} else {
					// Deuxième clic : créer la transition
					const symbol = prompt('Entrez le symbole de la transition (ou ε pour epsilon):');
					if (symbol !== null) {
						const newTransition: Transition = {
							id: crypto.randomUUID(),
							from: transitionStartState,
							to: clickedState,
							symbol: symbol.trim()
						};
						automaton.addTransition(newTransition);
						onTransitionAdded?.(newTransition);
					}
					transitionStartState = null;
				}
			}
		}
	}

	/**
	 * Trouve un état aux coordonnées données
	 */
	function findStateAt(x: number, y: number): State | null {
		return (
			$automaton.states.find((s) => {
				const dx = x - s.x;
				const dy = y - s.y;
				return Math.sqrt(dx * dx + dy * dy) <= STATE_RADIUS;
			}) || null
		);
	}

	/**
	 * Génère un nouveau label d'état (q0, q1, q2...)
	 */
	function generateStateLabel(): string {
		let counter = 0;
		while (true) {
			const label = `q${counter}`;
			if (!$automaton.states.some((s) => s.label === label)) {
				return label;
			}
			counter++;
		}
	}

	/**
	 * Calcule le chemin SVG pour une self-loop
	 */
	function getSelfLoopPath(state: State): string {
		const loopRadius = STATE_RADIUS * 0.7;
		const cx = state.x;
		const cy = state.y - STATE_RADIUS - loopRadius;

		// Arc circulaire au-dessus de l'état
		const startAngle = (75 * Math.PI) / 180;
		const endAngle = (465 * Math.PI) / 180; // 375° + 90° offset

		const startX = cx + loopRadius * Math.cos(startAngle);
		const startY = cy + loopRadius * Math.sin(startAngle);
		const endX = cx + loopRadius * Math.cos(endAngle);
		const endY = cy + loopRadius * Math.sin(endAngle);

		return `M ${startX} ${startY} A ${loopRadius} ${loopRadius} 0 1 1 ${endX} ${endY}`;
	}

	/**
	 * Calcule les coordonnées ajustées au bord du cercle
	 */
	function getEdgePoint(from: { x: number; y: number }, to: { x: number; y: number }) {
		const angle = Math.atan2(to.y - from.y, to.x - from.x);
		return {
			x: from.x + STATE_RADIUS * Math.cos(angle),
			y: from.y + STATE_RADIUS * Math.sin(angle)
		};
	}

	/**
	 * Vérifie s'il existe une transition dans le sens inverse
	 */
	function hasReverseTransition(from: State, to: State): boolean {
		return $automaton.transitions.some((t) => t.from.id === to.id && t.to.id === from.id);
	}

	/**
	 * Calcule le point de contrôle pour une courbe de Bézier
	 */
	function getBezierControlPoint(
		from: { x: number; y: number },
		to: { x: number; y: number }
	): { x: number; y: number } {
		const midX = (from.x + to.x) / 2;
		const midY = (from.y + to.y) / 2;
		const angle = Math.atan2(to.y - from.y, to.x - from.x);
		const perpAngle = angle + Math.PI / 2;
		const offset = 20;

		return {
			x: midX + offset * Math.cos(perpAngle),
			y: midY + offset * Math.sin(perpAngle)
		};
	}
</script>

<svg
	width="800"
	height="600"
	on:click={handleCanvasClick}
	class="border border-gray-300 bg-white cursor-crosshair"
>
	<!-- Définition de la flèche pour les transitions -->
	<defs>
		<marker
			id="arrowhead"
			markerWidth="10"
			markerHeight="10"
			refX="9"
			refY="3"
			orient="auto"
			markerUnits="strokeWidth"
		>
			<polygon points="0 0, 10 3, 0 6" fill="black" />
		</marker>
	</defs>

	<!-- Dessiner les transitions d'abord (sous les états) -->
	{#each $automaton.transitions as transition}
		{#if transition.from.id === transition.to.id}
			<!-- Self-loop -->
			<path
				d={getSelfLoopPath(transition.from)}
				fill="none"
				stroke={$selectedTransition?.id === transition.id ? '#3b82f6' : 'black'}
				stroke-width="1.5"
				marker-end="url(#arrowhead)"
			/>
			<text
				x={transition.from.x}
				y={transition.from.y - STATE_RADIUS - STATE_RADIUS * 0.7 * 2 - 5}
				text-anchor="middle"
				font-size="14"
			>
				{transition.symbol}
			</text>
		{:else if hasReverseTransition(transition.from, transition.to)}
			<!-- Transition bidirectionnelle : courbe de Bézier -->
			{@const start = getEdgePoint(transition.from, transition.to)}
			{@const end = getEdgePoint(transition.to, transition.from)}
			{@const control = getBezierControlPoint(transition.from, transition.to)}
			<path
				d={`M ${start.x} ${start.y} Q ${control.x} ${control.y} ${end.x} ${end.y}`}
				fill="none"
				stroke={$selectedTransition?.id === transition.id ? '#3b82f6' : 'black'}
				stroke-width="1.5"
				marker-end="url(#arrowhead)"
			/>
			<text x={control.x} y={control.y} text-anchor="middle" font-size="14">
				{transition.symbol}
			</text>
		{:else}
			<!-- Transition simple : ligne droite -->
			{@const start = getEdgePoint(transition.from, transition.to)}
			{@const end = getEdgePoint(transition.to, transition.from)}
			{@const midX = (start.x + end.x) / 2}
			{@const midY = (start.y + end.y) / 2}
			<line
				x1={start.x}
				y1={start.y}
				x2={end.x}
				y2={end.y}
				stroke={$selectedTransition?.id === transition.id ? '#3b82f6' : 'black'}
				stroke-width="1.5"
				marker-end="url(#arrowhead)"
			/>
			<text x={midX} y={midY - 5} text-anchor="middle" font-size="14">
				{transition.symbol}
			</text>
		{/if}
	{/each}

	<!-- Dessiner les états par-dessus -->
	{#each $automaton.states as state}
		<!-- Cercle principal -->
		<circle
			cx={state.x}
			cy={state.y}
			r={STATE_RADIUS}
			fill={$selectedState?.id === state.id
				? '#dbeafe'
				: transitionStartState?.id === state.id
					? '#fed7aa'
					: 'white'}
			stroke="black"
			stroke-width="1.5"
		/>

		<!-- Double cercle si acceptant -->
		{#if state.accepting}
			<circle
				cx={state.x}
				cy={state.y}
				r={STATE_RADIUS - 6}
				fill="none"
				stroke="black"
				stroke-width="1.5"
			/>
		{/if}

		<!-- Flèche entrante si initial -->
		{#if state.initial}
			<line
				x1={state.x - STATE_RADIUS - 50}
				y1={state.y}
				x2={state.x - STATE_RADIUS - 2}
				y2={state.y}
				stroke="black"
				stroke-width="1.5"
				marker-end="url(#arrowhead)"
			/>
		{/if}

		<!-- Label de l'état -->
		<text
			x={state.x}
			y={state.y}
			text-anchor="middle"
			dominant-baseline="middle"
			font-size="14"
			pointer-events="none"
		>
			{state.label}
		</text>
	{/each}
</svg>

<style>
	svg {
		display: block;
	}
</style>

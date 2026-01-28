import { writable } from 'svelte/store';
import type { Automaton, State, Transition, Tool } from '../types/automaton';

/**
 * Store principal de l'automate
 */
function createAutomatonStore() {
	const { subscribe, set, update } = writable<Automaton>({
		states: [],
		transitions: [],
		alphabet: [],
		name: 'Mon Automate'
	});

	return {
		subscribe,
		set,
		update,

		/**
		 * Réinitialise l'automate
		 */
		reset: () => set({
			states: [],
			transitions: [],
			alphabet: [],
			name: 'Mon Automate'
		}),

		/**
		 * Ajoute un état
		 */
		addState: (state: State) => update(a => ({
			...a,
			states: [...a.states, state]
		})),

		/**
		 * Supprime un état et ses transitions associées
		 */
		removeState: (stateId: string) => update(a => ({
			...a,
			states: a.states.filter(s => s.id !== stateId),
			transitions: a.transitions.filter(
				t => t.from.id !== stateId && t.to.id !== stateId
			)
		})),

		/**
		 * Met à jour un état
		 */
		updateState: (stateId: string, updates: Partial<State>) => update(a => ({
			...a,
			states: a.states.map(s =>
				s.id === stateId ? { ...s, ...updates } : s
			)
		})),

		/**
		 * Ajoute une transition
		 */
		addTransition: (transition: Transition) => update(a => {
			const newAlphabet = new Set(a.alphabet);
			if (transition.symbol && transition.symbol !== 'ε') {
				newAlphabet.add(transition.symbol);
			}
			return {
				...a,
				transitions: [...a.transitions, transition],
				alphabet: Array.from(newAlphabet).sort()
			};
		}),

		/**
		 * Supprime une transition
		 */
		removeTransition: (transitionId: string) => update(a => {
			const newTransitions = a.transitions.filter(t => t.id !== transitionId);
			// Recalculer l'alphabet
			const newAlphabet = new Set<string>();
			newTransitions.forEach(t => {
				if (t.symbol && t.symbol !== 'ε') {
					newAlphabet.add(t.symbol);
				}
			});
			return {
				...a,
				transitions: newTransitions,
				alphabet: Array.from(newAlphabet).sort()
			};
		})
	};
}

export const automaton = createAutomatonStore();

/**
 * Store de l'état sélectionné
 */
export const selectedState = writable<State | null>(null);

/**
 * Store de la transition sélectionnée
 */
export const selectedTransition = writable<Transition | null>(null);

/**
 * Store de l'outil courant
 */
export const currentTool = writable<Tool>('select');

/**
 * Store de l'ID de session
 */
export const sessionId = writable<string | null>(null);

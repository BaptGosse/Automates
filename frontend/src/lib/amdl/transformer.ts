/**
 * AMDL Transformer
 * Converts AMDL AST to JSON format compatible with the automaton API
 */

import type { AutomatonNode } from './parser';
import type { Automaton, State, Transition } from '../types/automaton';

export class Transformer {
	transform(ast: AutomatonNode): Automaton {
		const states: State[] = ast.states.map((stateNode, index) => ({
			id: this.generateUUID(), // Generate unique ID
			label: stateNode.label || stateNode.id,
			initial: stateNode.initial,
			accepting: stateNode.accepting,
			x: stateNode.position?.x ?? 100 + index * 150,
			y: stateNode.position?.y ?? 200
		}));

		// Create a map from state identifiers to generated IDs
		const stateIdMap = new Map<string, string>();
		ast.states.forEach((stateNode, index) => {
			stateIdMap.set(stateNode.id, states[index].id);
		});

		// Create transitions
		const transitions: Transition[] = [];

		for (const transNode of ast.transitions) {
			const fromId = stateIdMap.get(transNode.from);
			const toId = stateIdMap.get(transNode.to);

			if (!fromId) {
				throw new Error(`State '${transNode.from}' not found`);
			}
			if (!toId) {
				throw new Error(`State '${transNode.to}' not found`);
			}

			const fromState = states.find((s) => s.id === fromId)!;
			const toState = states.find((s) => s.id === toId)!;

			// Create one transition per symbol
			for (const symbol of transNode.symbols) {
				transitions.push({
					id: this.generateUUID(),
					from: fromState,
					to: toState,
					symbol
				});
			}
		}

		return {
			name: ast.name,
			states,
			transitions
		};
	}

	private generateUUID(): string {
		return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
			const r = (Math.random() * 16) | 0;
			const v = c === 'x' ? r : (r & 0x3) | 0x8;
			return v.toString(16);
		});
	}
}

/**
 * Types TypeScript correspondant aux modèles Java backend
 */

export interface State {
	id: string;
	label: string;
	x: number;
	y: number;
	initial: boolean;
	accepting: boolean;
}

export interface Transition {
	id: string;
	from: State;
	to: State;
	symbol: string;
}

export interface Automaton {
	states: State[];
	transitions: Transition[];
	alphabet: string[];
	name: string;
}

export interface AutomatonInfo {
	name: string;
	type: string;
	isDeterministic: boolean;
	isComplete: boolean;
	statesCount: number;
	transitionsCount: number;
	alphabet: string[];
	initialState: string | null;
	acceptingStates: string[];
	regex: string;
	languageDescription: string;
}

export type Tool = 'select' | 'addState' | 'addTransition';

export const STATE_RADIUS = 30;

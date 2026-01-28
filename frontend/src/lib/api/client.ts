import axios from 'axios';
import type { Automaton, State, Transition, AutomatonInfo } from '../types/automaton';

/**
 * Configuration de l'URL de l'API
 * - Mode Tauri desktop : récupération dynamique depuis le backend Rust
 * - Mode web développement : proxy Vite redirige vers localhost:8080
 * - Mode web production : variable d'environnement
 */

let API_BASE_URL = import.meta.env.VITE_API_URL || '/api';
let clientInitialized = false;

const client = axios.create({
	baseURL: API_BASE_URL,
	headers: {
		'Content-Type': 'application/json'
	}
});

/**
 * Détecte si on est en mode Tauri
 * En Tauri v2, il faut vérifier __TAURI_INTERNALS__ au lieu de __TAURI__
 */
function isTauriEnvironment(): boolean {
	if (typeof window === 'undefined') return false;

	// Tauri v2 injecte __TAURI_INTERNALS__
	const hasTauriInternals = '__TAURI_INTERNALS__' in window;
	// Tauri v1 injecte __TAURI__
	const hasTauri = '__TAURI__' in window;
	// Variable d'environnement
	const isTauriEnv = import.meta.env.TAURI_ENV_PLATFORM !== undefined;

	return hasTauriInternals || hasTauri || isTauriEnv;
}

/**
 * Initialise le client API (nécessaire en mode Tauri pour récupérer l'URL dynamique)
 */
async function initializeClient(): Promise<void> {
	if (clientInitialized) return;

	console.log('[API Client] Initializing...');
	console.log('[API Client] Window keys:', typeof window !== 'undefined' ? Object.keys(window).filter(k => k.includes('TAURI')) : 'no window');
	console.log('[API Client] import.meta.env.TAURI_ENV_PLATFORM:', import.meta.env.TAURI_ENV_PLATFORM);
	console.log('[API Client] Is Tauri?', isTauriEnvironment());
	console.log('[API Client] Current baseURL:', client.defaults.baseURL);

	if (isTauriEnvironment()) {
		try {
			console.log('[API Client] Tauri detected, fetching dynamic API URL...');
			// Import dynamique pour éviter les erreurs en mode web
			const { invoke } = await import('@tauri-apps/api/core');
			API_BASE_URL = await invoke<string>('get_api_url');
			client.defaults.baseURL = API_BASE_URL;
			console.log('[API Client] Tauri API URL retrieved:', API_BASE_URL);
		} catch (error) {
			console.error('[API Client] Error fetching Tauri API URL:', error);
		}
	} else {
		console.log('[API Client] Web mode, using baseURL:', client.defaults.baseURL);
	}

	clientInitialized = true;
}

// Intercepteur pour s'assurer que le client est initialisé avant chaque requête
client.interceptors.request.use(async (config) => {
	await initializeClient();
	console.log('[API Client] Request:', config.method?.toUpperCase(), config.url, 'baseURL:', client.defaults.baseURL);
	return config;
});

/**
 * Force l'initialisation du client (à appeler explicitement en mode Tauri)
 */
export async function ensureClientInitialized(): Promise<void> {
	await initializeClient();
}

/**
 * Client API pour communiquer avec le backend Spring Boot
 */
export const api = {
	/**
	 * Crée un nouvel automate
	 */
	async createAutomaton(name?: string): Promise<string> {
		const response = await client.post<{ sessionId: string }>('/automaton', { name });
		return response.data.sessionId;
	},

	/**
	 * Récupère un automate par son ID de session
	 */
	async getAutomaton(sessionId: string): Promise<Automaton> {
		const response = await client.get<Automaton>(`/automaton/${sessionId}`);
		return response.data;
	},

	/**
	 * Met à jour un automate complet
	 */
	async updateAutomaton(sessionId: string, automaton: Automaton): Promise<void> {
		await client.put(`/automaton/${sessionId}`, automaton);
	},

	/**
	 * Supprime un automate
	 */
	async deleteAutomaton(sessionId: string): Promise<void> {
		await client.delete(`/automaton/${sessionId}`);
	},

	/**
	 * Ajoute un état
	 */
	async addState(sessionId: string, x: number, y: number): Promise<State> {
		const response = await client.post<State>(`/automaton/${sessionId}/state`, { x, y });
		return response.data;
	},

	/**
	 * Supprime un état
	 */
	async removeState(sessionId: string, stateId: string): Promise<void> {
		await client.delete(`/automaton/${sessionId}/state/${stateId}`);
	},

	/**
	 * Met à jour un état
	 */
	async updateState(
		sessionId: string,
		stateId: string,
		updates: { x?: number; y?: number; initial?: boolean; accepting?: boolean }
	): Promise<void> {
		await client.put(`/automaton/${sessionId}/state/${stateId}`, updates);
	},

	/**
	 * Ajoute une transition
	 */
	async addTransition(
		sessionId: string,
		fromId: string,
		toId: string,
		symbol: string
	): Promise<Transition> {
		const response = await client.post<Transition>(`/automaton/${sessionId}/transition`, {
			fromId,
			toId,
			symbol
		});
		return response.data;
	},

	/**
	 * Supprime une transition
	 */
	async removeTransition(sessionId: string, transitionId: string): Promise<void> {
		await client.delete(`/automaton/${sessionId}/transition/${transitionId}`);
	},

	/**
	 * Récupère la table de transitions
	 */
	async getTransitionTable(sessionId: string): Promise<any> {
		const response = await client.get(`/automaton/${sessionId}/table`);
		return response.data;
	},

	/**
	 * Récupère les informations d'analyse de l'automate
	 */
	async getAutomatonInfo(sessionId: string): Promise<AutomatonInfo> {
		const response = await client.get<AutomatonInfo>(`/automaton/${sessionId}/info`);
		return response.data;
	}
};

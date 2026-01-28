<script lang="ts">
	import { onMount } from 'svelte';
	import AutomatonCanvas from '$lib/components/AutomatonCanvas.svelte';
	import Toolbar from '$lib/components/Toolbar.svelte';
	import TransitionTable from '$lib/components/TransitionTable.svelte';
	import InfoPanel from '$lib/components/InfoPanel.svelte';
	import { automaton, selectedState, sessionId } from '$lib/stores/automaton';
	import { api, ensureClientInitialized } from '$lib/api/client';
	import type { State, Transition } from '$lib/types/automaton';

	let infoPanelComponent: any;
	let statusMessage = 'Prêt';
	let backendReady = false;
	let loadingMessage = 'Initialisation...';
	let loadingError = false;

	// Détection environnement Tauri (v2 utilise __TAURI_INTERNALS__)
	const isTauri =
		typeof window !== 'undefined' &&
		('__TAURI_INTERNALS__' in window || '__TAURI__' in window);

	/**
	 * Initialise une nouvelle session au chargement
	 */
	onMount(async () => {
		// Vérifier si on est dans Tauri et attendre le démarrage du backend
		if (isTauri) {
			try {
				const { invoke } = await import('@tauri-apps/api/core');

				loadingMessage = 'Démarrage du backend Java...';

				// Attendre 3 secondes avant de commencer les health checks
				// (Spring Boot met du temps à initialiser)
				console.log('Attente de 3 secondes pour laisser le backend démarrer...');
				await new Promise(resolve => setTimeout(resolve, 3000));

				let attempts = 0;
				const maxAttempts = 30;

				while (attempts < maxAttempts) {
					try {
						console.log(`Health check tentative ${attempts + 1}/${maxAttempts}...`);
						const ready = await invoke<boolean>('is_backend_ready');
						console.log('Résultat health check:', ready);
						if (ready) {
							loadingMessage = 'Backend démarré avec succès !';
							backendReady = true;
							break;
						}
					} catch (e) {
						console.log('Backend pas encore prêt...', e);
					}

					attempts++;
					loadingMessage = `Démarrage du backend (${attempts}/${maxAttempts})...`;
					await new Promise(resolve => setTimeout(resolve, 1000));
				}

				if (!backendReady) {
					loadingMessage = 'Erreur: Le backend n\'a pas pu démarrer après 30 secondes';
					loadingError = true;
					return;
				}
			} catch (error) {
				console.error('Erreur lors de l\'initialisation Tauri:', error);
				loadingMessage = 'Erreur lors de l\'initialisation de l\'application desktop';
				loadingError = true;
				return;
			}
		} else {
			// Mode web, pas besoin d'attendre
			backendReady = true;
		}

		// En mode Tauri, initialiser explicitement le client API avec l'URL correcte
		if (isTauri) {
			console.log('Initialisation du client API avec l\'URL dynamique...');
			await ensureClientInitialized();
			console.log('Client API initialisé');
		}

		// Créer une session
		try {
			console.log('Création de la session...');
			const newSessionId = await api.createAutomaton('Mon Automate');
			$sessionId = newSessionId;
			statusMessage = 'Session créée';
			console.log('Session créée avec succès:', newSessionId);
		} catch (e) {
			statusMessage = 'Erreur lors de la création de la session';
			console.error('Erreur création session:', e);
		}
	});

	/**
	 * Gère l'ajout d'un état
	 */
	async function handleStateAdded(state: State) {
		if (!$sessionId) return;

		try {
			const newState = await api.addState($sessionId, state.x, state.y);
			// Mettre à jour l'état avec l'ID du serveur
			automaton.updateState(state.id, { id: newState.id, label: newState.label });
			statusMessage = `État ${newState.label} ajouté`;
			refreshInfo();
		} catch (e) {
			statusMessage = 'Erreur lors de l\'ajout de l\'état';
			console.error(e);
		}
	}

	/**
	 * Gère l'ajout d'une transition
	 */
	async function handleTransitionAdded(transition: Transition) {
		if (!$sessionId) return;

		try {
			await api.addTransition(
				$sessionId,
				transition.from.id,
				transition.to.id,
				transition.symbol
			);
			statusMessage = `Transition ajoutée: ${transition.from.label} --${transition.symbol}--> ${transition.to.label}`;
			refreshInfo();
		} catch (e) {
			statusMessage = 'Erreur lors de l\'ajout de la transition';
			console.error(e);
		}
	}

	/**
	 * Définit l'état sélectionné comme initial
	 */
	async function handleSetInitial() {
		if (!$selectedState || !$sessionId) return;

		try {
			// Retirer l'état initial des autres états
			$automaton.states.forEach(s => {
				if (s.id !== $selectedState?.id && s.initial) {
					automaton.updateState(s.id, { initial: false });
				}
			});

			// Définir le nouvel état initial
			const newInitial = !$selectedState.initial;
			automaton.updateState($selectedState.id, { initial: newInitial });

			await api.updateState($sessionId, $selectedState.id, { initial: newInitial });
			statusMessage = newInitial
				? `État ${$selectedState.label} défini comme initial`
				: `État ${$selectedState.label} n'est plus initial`;
			refreshInfo();
		} catch (e) {
			statusMessage = 'Erreur lors de la définition de l\'état initial';
			console.error(e);
		}
	}

	/**
	 * Bascule l'état acceptant de l'état sélectionné
	 */
	async function handleSetAccepting() {
		if (!$selectedState || !$sessionId) return;

		try {
			const newAccepting = !$selectedState.accepting;
			automaton.updateState($selectedState.id, { accepting: newAccepting });

			await api.updateState($sessionId, $selectedState.id, { accepting: newAccepting });
			statusMessage = newAccepting
				? `État ${$selectedState.label} défini comme acceptant`
				: `État ${$selectedState.label} n'est plus acceptant`;
			refreshInfo();
		} catch (e) {
			statusMessage = 'Erreur lors de la définition de l\'état acceptant';
			console.error(e);
		}
	}

	/**
	 * Supprime l'état sélectionné
	 */
	async function handleDelete() {
		if (!$selectedState || !$sessionId) return;

		try {
			const stateLabel = $selectedState.label;
			await api.removeState($sessionId, $selectedState.id);
			automaton.removeState($selectedState.id);
			$selectedState = null;
			statusMessage = `État ${stateLabel} supprimé`;
			refreshInfo();
		} catch (e) {
			statusMessage = 'Erreur lors de la suppression';
			console.error(e);
		}
	}

	/**
	 * Efface tout l'automate
	 */
	async function handleClear() {
		if (!$sessionId) return;

		try {
			// Créer une nouvelle session
			const newSessionId = await api.createAutomaton('Mon Automate');
			$sessionId = newSessionId;
			automaton.reset();
			statusMessage = 'Automate effacé';
			refreshInfo();
		} catch (e) {
			statusMessage = 'Erreur lors de l\'effacement';
			console.error(e);
		}
	}

	/**
	 * Rafraîchit le panneau d'informations
	 */
	function refreshInfo() {
		if (infoPanelComponent?.refresh) {
			infoPanelComponent.refresh();
		}
	}
</script>

<svelte:head>
	<title>Éditeur d'Automates</title>
</svelte:head>

{#if !backendReady}
	<!-- Écran de chargement pour le mode Tauri -->
	<div class="loading-screen">
		<div class="loading-content">
			<h1 class="loading-title">Éditeur d'Automates</h1>

			{#if !loadingError}
				<div class="loading-spinner">
					<div class="spinner"></div>
				</div>
				<p class="loading-message">{loadingMessage}</p>
			{:else}
				<div class="error-icon">⚠️</div>
				<p class="error-message">{loadingMessage}</p>
				<p class="error-hint">Assurez-vous que Java est installé sur votre système.</p>
			{/if}
		</div>
	</div>
{:else}
	<!-- Application normale -->
	<div class="app-container">
		<!-- En-tête -->
		<header class="header">
			<h1 class="title">Éditeur d'Automates</h1>
		</header>

	<!-- Barre d'outils -->
	<Toolbar
		onClear={handleClear}
		onSetInitial={handleSetInitial}
		onSetAccepting={handleSetAccepting}
		onDelete={handleDelete}
	/>

	<!-- Contenu principal -->
	<div class="main-content">
		<!-- Canvas -->
		<div class="canvas-container">
			<AutomatonCanvas
				onStateAdded={handleStateAdded}
				onTransitionAdded={handleTransitionAdded}
			/>
		</div>

		<!-- Panneaux latéraux -->
		<div class="side-panels">
			<div class="panel">
				<TransitionTable />
			</div>
			<div class="panel">
				<InfoPanel bind:this={infoPanelComponent} />
			</div>
		</div>
	</div>

		<!-- Barre de statut -->
		<footer class="status-bar">
			<span class="status-message">{statusMessage}</span>
			<span class="status-info">
				{$automaton.states.length} état{$automaton.states.length !== 1 ? 's' : ''},
				{$automaton.transitions.length} transition{$automaton.transitions.length !== 1 ? 's' : ''}
			</span>
		</footer>
	</div>
{/if}

<style>
	/* Écran de chargement */
	.loading-screen {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 100vh;
		background: linear-gradient(135deg, #1f2937 0%, #374151 100%);
	}

	.loading-content {
		text-align: center;
		color: white;
	}

	.loading-title {
		font-size: 2.5rem;
		font-weight: 700;
		margin-bottom: 2rem;
		color: #fff;
	}

	.loading-spinner {
		display: flex;
		justify-content: center;
		margin-bottom: 1.5rem;
	}

	.spinner {
		width: 50px;
		height: 50px;
		border: 4px solid rgba(255, 255, 255, 0.3);
		border-top-color: #3b82f6;
		border-radius: 50%;
		animation: spin 1s linear infinite;
	}

	@keyframes spin {
		to {
			transform: rotate(360deg);
		}
	}

	.loading-message {
		font-size: 1.125rem;
		color: rgba(255, 255, 255, 0.9);
	}

	.error-icon {
		font-size: 4rem;
		margin-bottom: 1rem;
	}

	.error-message {
		font-size: 1.125rem;
		color: #fca5a5;
		margin-bottom: 0.5rem;
	}

	.error-hint {
		font-size: 0.875rem;
		color: rgba(255, 255, 255, 0.7);
	}

	/* Application */
	.app-container {
		display: flex;
		flex-direction: column;
		height: 100vh;
		overflow: hidden;
	}

	.header {
		background-color: #1f2937;
		color: white;
		padding: 1rem 1.5rem;
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	}

	.title {
		margin: 0;
		font-size: 1.5rem;
		font-weight: 600;
	}

	.main-content {
		flex: 1;
		display: flex;
		overflow: hidden;
	}

	.canvas-container {
		flex: 1;
		padding: 1rem;
		overflow: auto;
		background-color: #f9fafb;
	}

	.side-panels {
		width: 400px;
		display: flex;
		flex-direction: column;
		gap: 1rem;
		padding: 1rem;
		background-color: #f3f4f6;
		overflow-y: auto;
		border-left: 1px solid #d1d5db;
	}

	.panel {
		flex-shrink: 0;
	}

	.status-bar {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 0.5rem 1rem;
		background-color: #f3f4f6;
		border-top: 1px solid #d1d5db;
		font-size: 0.875rem;
	}

	.status-message {
		color: #4b5563;
	}

	.status-info {
		color: #6b7280;
	}
</style>

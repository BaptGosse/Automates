import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
	plugins: [sveltekit()],
	server: {
		port: 5173,
		// Proxy pour le mode web développement
		// En mode Tauri, le client API utilisera l'URL absolue récupérée depuis Rust
		proxy: {
			'/api': {
				target: 'http://localhost:8080',
				changeOrigin: true
			}
		}
	},
	// Variables d'environnement pour détecter Tauri
	define: {
		'import.meta.env.TAURI_ENV_PLATFORM': JSON.stringify(process.env.TAURI_ENV_PLATFORM)
	}
});

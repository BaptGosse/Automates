# Phases Restantes - Automates Fullstack

## ✅ Phases Complétées

- ✅ **Phase 1 : Backend Spring Boot** - API REST complète avec analyse
- ✅ **Phase 2 : Frontend Svelte** - Interface web interactive avec canvas SVG

---

## 🚀 Phase 3 : Desktop Tauri (100% Offline)

### Objectif
Créer une application desktop native qui embarque le backend Java pour fonctionner complètement offline.

### Prérequis
- Rust 1.70+ installé
- Node.js 18+
- Backend JAR compilé

### Étapes Détaillées

#### 3.1 Setup Tauri (30 min)

**Créer le projet Tauri :**
```bash
cd /run/media/baptgosse/Unified_Datas/BaptGosse/Documents/OtherProjects/Automates
npm create tauri-app desktop
# Choisir : Svelte, TypeScript
```

**Installer Tauri CLI :**
```bash
cd desktop
npm install --save-dev @tauri-apps/cli
npm install @tauri-apps/api
```

#### 3.2 Configuration Tauri (1h)

**Fichier `desktop/src-tauri/tauri.conf.json` :**
```json
{
  "build": {
    "beforeDevCommand": "npm run dev --prefix ../frontend",
    "beforeBuildCommand": "npm run build --prefix ../frontend",
    "devPath": "http://localhost:5173",
    "distDir": "../frontend/build"
  },
  "package": {
    "productName": "Automates Editor",
    "version": "1.0.0"
  },
  "tauri": {
    "allowlist": {
      "all": false,
      "shell": {
        "sidecar": true,
        "scope": [
          { "name": "java", "sidecar": true, "args": true }
        ]
      },
      "path": {
        "all": true
      },
      "fs": {
        "scope": ["$RESOURCE/*"]
      }
    },
    "bundle": {
      "active": true,
      "category": "DeveloperTool",
      "copyright": "© 2026 Baptiste Gosselin",
      "resources": [
        "resources/backend.jar"
      ],
      "externalBin": [],
      "targets": ["deb", "appimage", "msi"],
      "windows": {
        "certificateThumbprint": null,
        "digestAlgorithm": "sha256",
        "timestampUrl": ""
      }
    },
    "security": {
      "csp": null
    },
    "windows": [
      {
        "title": "Éditeur d'Automates",
        "width": 1200,
        "height": 800,
        "resizable": true,
        "fullscreen": false
      }
    ]
  }
}
```

#### 3.3 Backend Rust pour Démarrage Java (2h)

**Créer la structure :**
```bash
mkdir -p desktop/src-tauri/resources
```

**Fichier `desktop/src-tauri/src/main.rs` :**
```rust
#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use std::process::{Command, Child};
use std::net::TcpListener;
use std::sync::{Arc, Mutex};
use tauri::{Manager, State};

struct BackendProcess {
    child: Arc<Mutex<Option<Child>>>,
    port: u16,
}

/// Trouve un port TCP libre
fn find_free_port() -> u16 {
    TcpListener::bind("127.0.0.1:0")
        .unwrap()
        .local_addr()
        .unwrap()
        .port()
}

/// Démarre le backend Spring Boot
fn start_backend(port: u16, resource_dir: &std::path::Path) -> std::io::Result<Child> {
    let jar_path = resource_dir.join("backend.jar");

    println!("Démarrage du backend Java sur le port {}", port);
    println!("JAR path: {:?}", jar_path);

    Command::new("java")
        .arg("-jar")
        .arg(&jar_path)
        .arg(format!("--server.port={}", port))
        .arg("--spring.profiles.active=desktop")
        .arg("--logging.level.root=INFO")
        .spawn()
}

/// Commande Tauri pour obtenir l'URL de l'API
#[tauri::command]
fn get_api_url(state: State<BackendProcess>) -> String {
    format!("http://localhost:{}/api", state.port)
}

/// Commande Tauri pour vérifier si le backend est prêt
#[tauri::command]
async fn is_backend_ready(state: State<'_, BackendProcess>) -> Result<bool, String> {
    let url = format!("http://localhost:{}/actuator/health", state.port);
    match reqwest::get(&url).await {
        Ok(response) => Ok(response.status().is_success()),
        Err(_) => Ok(false),
    }
}

fn main() {
    let port = find_free_port();

    tauri::Builder::default()
        .setup(|app| {
            let resource_path = app.path_resolver()
                .resource_dir()
                .expect("failed to resolve resource dir");

            match start_backend(port, &resource_path) {
                Ok(child) => {
                    println!("Backend démarré avec succès");
                    app.manage(BackendProcess {
                        child: Arc::new(Mutex::new(Some(child))),
                        port,
                    });
                },
                Err(e) => {
                    eprintln!("Erreur lors du démarrage du backend: {}", e);
                    return Err(Box::new(e));
                }
            }

            Ok(())
        })
        .invoke_handler(tauri::generate_handler![get_api_url, is_backend_ready])
        .on_window_event(|event| {
            if let tauri::WindowEvent::Destroyed = event.event() {
                // Arrêter le backend proprement
                println!("Fermeture de l'application, arrêt du backend...");
                if let Some(state) = event.window().try_state::<BackendProcess>() {
                    if let Ok(mut child_lock) = state.child.lock() {
                        if let Some(mut child) = child_lock.take() {
                            let _ = child.kill();
                            println!("Backend arrêté");
                        }
                    }
                }
            }
        })
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
```

**Fichier `desktop/src-tauri/Cargo.toml` :**
```toml
[package]
name = "automates-desktop"
version = "1.0.0"
description = "Éditeur d'automates - Application Desktop"
authors = ["Baptiste Gosselin"]
license = ""
repository = ""
edition = "2021"

[build-dependencies]
tauri-build = { version = "1.5", features = [] }

[dependencies]
tauri = { version = "1.5", features = ["shell-sidecar"] }
serde = { version = "1.0", features = ["derive"] }
serde_json = "1.0"
reqwest = { version = "0.11", features = ["blocking"] }

[features]
custom-protocol = ["tauri/custom-protocol"]
```

#### 3.4 Adaptation Frontend pour Tauri (1h)

**Modifier `frontend/src/lib/api/client.ts` :**
```typescript
import axios from 'axios';
import type { Automaton, State, Transition, AutomatonInfo } from '../types/automaton';

// Détection environnement Tauri
const isTauri = typeof window !== 'undefined' && '__TAURI__' in window;

let API_BASE_URL = '/api';

// Si Tauri, récupérer l'URL depuis le backend Rust
if (isTauri) {
    const { invoke } = await import('@tauri-apps/api/tauri');
    API_BASE_URL = await invoke<string>('get_api_url');
}

const client = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

// ... reste du code identique
```

**Ajouter un indicateur de chargement dans `frontend/src/routes/+page.svelte` :**
```svelte
<script lang="ts">
    import { onMount } from 'svelte';

    let backendReady = false;
    let loadingMessage = 'Démarrage du backend...';

    onMount(async () => {
        // Vérifier si on est dans Tauri
        if (typeof window !== 'undefined' && '__TAURI__' in window) {
            const { invoke } = await import('@tauri-apps/api/tauri');

            // Attendre que le backend soit prêt
            let attempts = 0;
            const maxAttempts = 30;

            while (attempts < maxAttempts) {
                try {
                    const ready = await invoke<boolean>('is_backend_ready');
                    if (ready) {
                        backendReady = true;
                        break;
                    }
                } catch (e) {
                    console.log('Backend pas encore prêt...');
                }

                attempts++;
                loadingMessage = `Démarrage du backend (${attempts}/${maxAttempts})...`;
                await new Promise(resolve => setTimeout(resolve, 1000));
            }

            if (!backendReady) {
                loadingMessage = 'Erreur: Le backend n\'a pas pu démarrer';
                return;
            }
        } else {
            backendReady = true; // Mode web
        }

        // Initialiser la session
        // ... code existant
    });
</script>

{#if !backendReady}
    <div class="loading-screen">
        <h1>Éditeur d'Automates</h1>
        <p>{loadingMessage}</p>
    </div>
{:else}
    <!-- Contenu normal de l'application -->
{/if}
```

#### 3.5 Script de Build Desktop (30 min)

**Créer `scripts/build-desktop.sh` :**
```bash
#!/bin/bash
set -e

echo "🏗️  Building Desktop Application..."

# 1. Build backend JAR
echo "📦 Building backend JAR..."
cd backend
mvn clean package -DskipTests
JAR_FILE=$(ls target/automates-backend.jar)

# 2. Copier le JAR dans les ressources Tauri
echo "📋 Copying JAR to Tauri resources..."
mkdir -p ../desktop/src-tauri/resources
cp "$JAR_FILE" ../desktop/src-tauri/resources/backend.jar

# 3. Build frontend
echo "🎨 Building frontend..."
cd ../frontend
npm run build

# 4. Build Tauri app
echo "🚀 Building Tauri application..."
cd ../desktop
npm run tauri build

echo "✅ Build complete!"
echo "📦 Desktop app location: desktop/src-tauri/target/release/bundle/"
```

**Rendre exécutable :**
```bash
chmod +x scripts/build-desktop.sh
```

#### 3.6 Tests et Vérification

**Développement :**
```bash
# Terminal 1 : Backend
cd backend
mvn spring-boot:run

# Terminal 2 : Frontend + Tauri
cd desktop
npm run tauri dev
```

**Build Production :**
```bash
./scripts/build-desktop.sh
```

**Vérifications :**
- [ ] L'application desktop se lance
- [ ] Le backend Java démarre automatiquement
- [ ] Le port est détecté dynamiquement
- [ ] L'interface se charge correctement
- [ ] Les fonctionnalités fonctionnent (ajout états, transitions)
- [ ] L'application fonctionne sans connexion internet
- [ ] La fermeture arrête proprement le backend
- [ ] Le JAR est bien embarqué dans l'installateur

---

## 📱 Phase 4 : Mobile Capacitor

### Objectif
Créer des applications mobiles natives iOS et Android qui communiquent avec le backend web hébergé.

### Prérequis
- Android Studio (pour Android)
- Xcode (pour iOS, macOS uniquement)
- Backend déployé sur un serveur accessible

### Étapes Détaillées

#### 4.1 Setup Capacitor (30 min)

**Installer Capacitor :**
```bash
cd frontend
npm install @capacitor/core @capacitor/cli
npm install @capacitor/android @capacitor/ios
```

**Initialiser Capacitor :**
```bash
npx cap init
# App name: Automates Editor
# App ID: fr.baptgosse.automates
# Web directory: build
```

#### 4.2 Configuration Capacitor (1h)

**Créer `mobile/capacitor.config.ts` :**
```typescript
import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'fr.baptgosse.automates',
  appName: 'Éditeur d\'Automates',
  webDir: '../frontend/build',
  server: {
    androidScheme: 'https',
    // URL du backend en production
    url: process.env.VITE_API_URL || 'https://automates-api.baptiste-gosselin.fr',
    cleartext: false
  },
  plugins: {
    SplashScreen: {
      launchShowDuration: 2000,
      backgroundColor: "#1f2937",
      showSpinner: true,
      androidSpinnerStyle: "large",
      iosSpinnerStyle: "small",
      spinnerColor: "#3b82f6"
    }
  }
};

export default config;
```

#### 4.3 Adaptations UI Mobile (2h)

**Créer styles responsive dans `frontend/src/app.css` :**
```css
/* Variables CSS */
:root {
  --canvas-width: 800px;
  --canvas-height: 600px;
  --state-radius: 30px;
  --toolbar-height: 60px;
}

/* Mobile styles */
@media (max-width: 768px) {
  :root {
    --canvas-width: 100vw;
    --canvas-height: 60vh;
    --state-radius: 25px;
    --toolbar-height: auto;
  }

  .app-container {
    flex-direction: column;
  }

  .side-panels {
    width: 100%;
    height: 40vh;
    flex-direction: row;
    overflow-x: auto;
  }

  .panel {
    min-width: 300px;
    flex-shrink: 0;
  }

  .toolbar {
    flex-wrap: wrap;
  }

  .tool-button {
    min-width: 44px;
    min-height: 44px;
  }
}

/* Touch targets */
@media (pointer: coarse) {
  .tool-button,
  button {
    min-width: 44px;
    min-height: 44px;
  }
}
```

**Modifier `AutomatonCanvas.svelte` pour touch events :**
```svelte
<script lang="ts">
    // ... code existant

    function handleTouchStart(event: TouchEvent) {
        event.preventDefault();
        const touch = event.touches[0];
        const svg = event.currentTarget as SVGSVGElement;
        const rect = svg.getBoundingClientRect();
        const x = touch.clientX - rect.left;
        const y = touch.clientY - rect.top;

        // Simuler un clic
        handleCanvasClick({ clientX: touch.clientX, clientY: touch.clientY } as any);
    }
</script>

<svg
    width="800"
    height="600"
    on:click={handleCanvasClick}
    on:touchstart={handleTouchStart}
    class="border border-gray-300 bg-white cursor-crosshair"
>
```

#### 4.4 Build Android (1h)

**Ajouter plateforme Android :**
```bash
cd mobile
npx cap add android
```

**Synchroniser :**
```bash
npm run build --prefix ../frontend
npx cap sync
```

**Ouvrir dans Android Studio :**
```bash
npx cap open android
```

**Dans Android Studio :**
1. Vérifier `android/app/src/main/AndroidManifest.xml` :
   - Permissions internet
   - `android:usesCleartextTraffic="true"` pour développement
2. Build → Generate Signed Bundle / APK
3. Créer keystore si nécessaire
4. Générer APK release

#### 4.5 Build iOS (1h) - macOS uniquement

**Ajouter plateforme iOS :**
```bash
npx cap add ios
npx cap sync
```

**Ouvrir dans Xcode :**
```bash
npx cap open ios
```

**Dans Xcode :**
1. Configurer signing & capabilities
2. Ajouter App Transport Security si nécessaire
3. Product → Archive
4. Distribuer sur TestFlight ou App Store

#### 4.6 Script de Build Mobile (30 min)

**Créer `scripts/build-mobile.sh` :**
```bash
#!/bin/bash
set -e

echo "📱 Building Mobile Application..."

# 1. Build frontend avec URL backend production
echo "🎨 Building frontend..."
cd frontend
VITE_API_URL=https://automates-api.baptiste-gosselin.fr npm run build

# 2. Sync Capacitor
echo "🔄 Syncing Capacitor..."
cd ../mobile
npx cap sync

echo "✅ Mobile sync complete!"
echo "📱 Next steps:"
echo "   - Android: npx cap open android (puis Build → Generate Signed Bundle)"
echo "   - iOS: npx cap open ios (puis Product → Archive)"
```

#### 4.7 Tests Mobile

**Tests de base :**
- [ ] L'application se lance sur émulateur
- [ ] Le canvas s'affiche correctement
- [ ] Les touch events fonctionnent
- [ ] Les boutons sont assez grands (44x44px minimum)
- [ ] L'UI s'adapte à l'orientation
- [ ] La communication avec le backend web fonctionne
- [ ] Les transitions sont fluides

**Tests sur device réel :**
- [ ] Installer APK sur Android physique
- [ ] Tester sur iPhone via TestFlight
- [ ] Vérifier performance
- [ ] Tester hors ligne (doit afficher erreur propre)

---

## 🎯 Checklist Complète

### Phase 3 - Desktop
- [ ] Setup Tauri
- [ ] Configuration tauri.conf.json
- [ ] Backend Rust pour lancement Java
- [ ] Détection port libre
- [ ] Copie JAR dans resources
- [ ] Adaptation client API frontend
- [ ] Écran de chargement
- [ ] Tests développement
- [ ] Build production
- [ ] Vérification offline
- [ ] Installateur Linux (.deb, .appimage)
- [ ] Installateur Windows (.msi)

### Phase 4 - Mobile
- [ ] Setup Capacitor
- [ ] Configuration capacitor.config.ts
- [ ] Styles responsive CSS
- [ ] Touch events canvas
- [ ] Boutons 44x44px minimum
- [ ] Layout mobile
- [ ] Build Android APK
- [ ] Build iOS IPA
- [ ] Tests émulateur
- [ ] Tests device réel
- [ ] Soumission stores (optionnel)

---

## 📝 Notes Importantes

### Déploiement Backend Web (Prérequis Phase 4)

Pour que le mobile fonctionne, le backend doit être hébergé :

**Options :**
1. **Heroku** (gratuit tier) :
   ```bash
   heroku create automates-api
   git push heroku main
   ```

2. **Railway** :
   - Connecter repo GitHub
   - Déploiement automatique

3. **VPS** (OVH, Scaleway, etc.) :
   ```bash
   # Copier JAR sur serveur
   scp backend/target/automates-backend.jar user@server:/app/

   # Sur serveur
   java -jar /app/automates-backend.jar --server.port=8080
   ```

4. **Docker** :
   ```dockerfile
   FROM openjdk:21-jdk-slim
   COPY target/automates-backend.jar /app/app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "/app/app.jar"]
   ```

### Variables d'Environnement

**Développement :**
- `VITE_API_URL=http://localhost:8080/api`

**Production Web :**
- `VITE_API_URL=https://automates-api.baptiste-gosselin.fr/api`

**Desktop :**
- Détection automatique via Tauri invoke

**Mobile :**
- Hardcodé dans capacitor.config.ts

---

## ⏱️ Estimation Temps

**Phase 3 - Desktop :**
- Setup et config : 2h
- Backend Rust : 2h
- Adaptation frontend : 1h
- Build et tests : 1h
- **Total : ~6h (1 journée)**

**Phase 4 - Mobile :**
- Setup Capacitor : 1h
- Adaptations UI : 2h
- Build Android : 1h
- Build iOS : 1h
- Tests : 1h
- **Total : ~6h (1 journée)**

**Total Phase 3+4 : ~12h (2 jours de travail)**

---

## 🔗 Ressources

- [Tauri Docs](https://tauri.app/v1/guides/)
- [Capacitor Docs](https://capacitorjs.com/docs)
- [SvelteKit Adapters](https://kit.svelte.dev/docs/adapters)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

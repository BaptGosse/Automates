# Guide des Scripts - Automates Project

Collection complète de scripts pour faciliter le développement et le déploiement.

## 🚀 Quick Start

### Premier lancement

```bash
# 1. Setup initial (une seule fois)
./scripts/setup.sh

# 2. Choisir votre plateforme
./scripts/dev-web.sh      # Application web
./scripts/dev-desktop.sh  # Application desktop
```

---

## 📋 Liste des Scripts

### Setup

| Script | Description | Usage |
|--------|-------------|-------|
| `setup.sh` | Configuration initiale du projet | `./scripts/setup.sh` |

### Développement

| Script | Description | Serveurs | Usage |
|--------|-------------|----------|-------|
| `dev-backend.sh` | Backend seul | :8080 | `./scripts/dev-backend.sh` |
| `dev-frontend.sh` | Frontend seul | :5173 | `./scripts/dev-frontend.sh` |
| `dev-web.sh` | Backend + Frontend | :8080 + :5173 | `./scripts/dev-web.sh` |
| `dev-desktop.sh` | Application Tauri | Fenêtre native | `./scripts/dev-desktop.sh` |

### Build Production

| Script | Description | Sortie | Usage |
|--------|-------------|--------|-------|
| `build-backend.sh` | JAR Spring Boot | `backend/target/*.jar` | `./scripts/build-backend.sh` |
| `build-frontend.sh` | Build Svelte | `frontend/build/` | `./scripts/build-frontend.sh` |
| `build-web.sh` | Backend + Frontend | JAR + build/ | `./scripts/build-web.sh` |
| `build-desktop.sh` | Tauri (Linux/Win) | `.deb`, `.appimage`, `.msi` | `./scripts/build-desktop.sh` |
| `build-mobile-android.sh` | APK Android | `.apk` | `./scripts/build-mobile-android.sh` |
| `build-mobile-ios.sh` | IPA iOS | `.ipa` | `./scripts/build-mobile-ios.sh` |
| `build-all.sh` | **Tout** (sauf mobile) | Tous | `./scripts/build-all.sh` |

### Utilitaires

| Script | Description | Usage |
|--------|-------------|-------|
| `clean.sh` | Nettoie tous les builds | `./scripts/clean.sh` |
| `test.sh` | Lance tous les tests | `./scripts/test.sh` |

---

## 🎯 Cas d'Usage

### Je veux développer l'application web

```bash
# Option 1 : Tout automatique
./scripts/dev-web.sh

# Option 2 : Manuellement dans 2 terminaux
Terminal 1: ./scripts/dev-backend.sh
Terminal 2: ./scripts/dev-frontend.sh
```

Ouvrir http://localhost:5173 dans le navigateur.

---

### Je veux développer l'application desktop

```bash
./scripts/dev-desktop.sh
```

Une fenêtre native s'ouvre. Le backend Java démarre automatiquement.

---

### Je veux créer un installateur desktop

```bash
./scripts/build-desktop.sh
```

Fichiers générés :
- **Linux** : `desktop/src-tauri/target/release/bundle/deb/*.deb`
- **Linux** : `desktop/src-tauri/target/release/bundle/appimage/*.AppImage`
- **Windows** : `desktop/src-tauri/target/release/bundle/msi/*.msi`

---

### Je veux déployer l'application web

```bash
# 1. Build
./scripts/build-web.sh

# 2. Déployer le backend
scp backend/target/automates-backend.jar user@server:/opt/automates/
ssh user@server "java -jar /opt/automates/automates-backend.jar"

# 3. Déployer le frontend
rsync -av frontend/build/ user@server:/var/www/automates/
```

---

### Je veux tout builder d'un coup

```bash
./scripts/build-all.sh
```

Résumé affiché :
- ✅ Succès de chaque build
- ⏱️ Temps total
- 📦 Emplacements des fichiers

---

### Je veux nettoyer avant un rebuild

```bash
./scripts/clean.sh
./scripts/build-all.sh
```

---

### Je veux vérifier que tout marche

```bash
./scripts/test.sh
```

---

## 🔧 Prérequis par Plateforme

### Web (Backend + Frontend)

- ✅ Java 21+
- ✅ Maven 3.6+
- ✅ Node.js 18+
- ✅ npm 8+

### Desktop (Tauri)

Prérequis Web +
- ✅ Rust 1.70+
- ✅ Cargo

### Mobile Android

Prérequis Web +
- ✅ Android Studio
- ✅ Android SDK

### Mobile iOS

Prérequis Web +
- ✅ macOS
- ✅ Xcode
- ✅ CocoaPods

---

## 📁 Structure après Build

```
Automates/
├── backend/
│   └── target/
│       └── automates-backend.jar        # 19 MB
│
├── frontend/
│   └── build/                           # ~2 MB
│       ├── index.html
│       ├── _app/
│       └── ...
│
└── desktop/
    └── src-tauri/
        └── target/
            └── release/
                └── bundle/
                    ├── deb/             # Debian package
                    ├── appimage/        # Linux portable
                    └── msi/             # Windows installer
```

---

## ⚡ Tips & Tricks

### Accélérer les builds

```bash
# Backend : skip tests
cd backend && mvn package -DskipTests

# Frontend : build sans optimisations
cd frontend && npm run build -- --mode development

# Desktop : build debug (plus rapide)
cd desktop && npm run tauri build -- --debug
```

### Build pour une plateforme spécifique

```bash
# Desktop : Linux .deb seulement
cd desktop
npm run tauri build -- --bundles deb

# Desktop : Linux AppImage seulement
npm run tauri build -- --bundles appimage
```

### Changer l'URL de l'API backend

```bash
# Frontend
VITE_API_URL=https://api.production.com ./scripts/build-frontend.sh

# Mobile Android
export VITE_API_URL=https://api.production.com
./scripts/build-mobile-android.sh
```

### Lancer le backend sur un autre port

```bash
# Mode dev
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9000

# JAR
java -jar backend.jar --server.port=9000
```

---

## 🐛 Troubleshooting

### Erreur "Command not found: cargo"

```bash
source $HOME/.cargo/env
# ou
export PATH="$HOME/.cargo/bin:$PATH"
```

### Erreur "Port 8080 already in use"

```bash
# Trouver le processus
lsof -i :8080

# Tuer
kill -9 <PID>
```

### Erreur "Backend JAR not found"

```bash
cd backend
mvn package -DskipTests
cp target/automates-backend.jar ../desktop/src-tauri/resources/
```

### Build Tauri échoue avec erreur Rust

```bash
cd desktop/src-tauri
cargo clean
cargo build
```

---

## 📚 Documentation Détaillée

Pour plus de détails, consultez :
- `scripts/README.md` - Documentation complète des scripts
- `desktop/README.md` - Guide desktop Tauri
- `docs/PHASE3-COMPLETE.md` - Détails de la Phase 3

---

## ✅ Checklist de Développement

### Nouveau développeur

- [ ] Cloner le repo
- [ ] Lancer `./scripts/setup.sh`
- [ ] Lancer `./scripts/dev-web.sh` ou `./scripts/dev-desktop.sh`
- [ ] Faire des modifications
- [ ] Lancer `./scripts/test.sh`
- [ ] Commit

### Avant une release

- [ ] Lancer `./scripts/test.sh`
- [ ] Lancer `./scripts/clean.sh`
- [ ] Lancer `./scripts/build-all.sh`
- [ ] Tester les builds
- [ ] Tag la version
- [ ] Distribuer les installateurs

---

**Tous les scripts sont prêts ! 🎉**

Happy coding! 🚀

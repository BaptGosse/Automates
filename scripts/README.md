# Scripts - Automates Project

Collection complète de scripts pour le développement, le build et la maintenance du projet Automates.

## Table des matières

- [Setup Initial](#setup-initial)
- [Scripts de Développement](#scripts-de-développement)
- [Scripts de Build](#scripts-de-build)
- [Scripts Utilitaires](#scripts-utilitaires)
- [Structure du Projet](#structure-du-projet)

---

## Setup Initial

### `setup.sh` - Configuration initiale du projet

**Usage :**
```bash
./scripts/setup.sh
```

**Ce qu'il fait :**
- ✅ Vérifie les prérequis (Java, Maven, Node.js, npm, Rust)
- 📦 Installe toutes les dépendances npm
- 🎯 Prépare l'environnement de développement

**Quand l'utiliser :**
- Après avoir cloné le projet
- Après une réinstallation système
- Pour vérifier que l'environnement est correct

---

## Scripts de Développement

Ces scripts lancent les serveurs de développement avec hot-reload.

### `dev-backend.sh` - Backend Spring Boot seul

**Usage :**
```bash
./scripts/dev-backend.sh
```

**Serveurs démarrés :**
- Backend API : http://localhost:8080
- Health check : http://localhost:8080/actuator/health

**Technologies :**
- Spring Boot avec Maven
- Hot reload automatique

---

### `dev-frontend.sh` - Frontend Svelte seul

**Usage :**
```bash
./scripts/dev-frontend.sh
```

**Prérequis :**
- Le backend doit tourner sur le port 8080

**Serveurs démarrés :**
- Frontend : http://localhost:5173
- Vite proxy : /api → http://localhost:8080/api

**Technologies :**
- SvelteKit avec Vite
- Hot module replacement (HMR)

---

### `dev-web.sh` - Backend + Frontend ensemble

**Usage :**
```bash
./scripts/dev-web.sh
```

**Ce qu'il fait :**
- Ouvre 2 terminaux automatiquement
- Terminal 1 : Backend sur le port 8080
- Terminal 2 : Frontend sur le port 5173
- Attend 10s entre les deux pour que le backend démarre

**Terminaux supportés :**
- gnome-terminal
- konsole
- xterm

**Pour arrêter :**
- Ctrl+C dans chaque terminal

---

### `dev-desktop.sh` - Application Desktop Tauri

**Usage :**
```bash
./scripts/dev-desktop.sh
```

**Prérequis :**
- Rust installé (`rustup`)
- Java 21+ (pour exécuter le backend embarqué)

**Ce qu'il fait :**
- Vérifie que le JAR backend existe (sinon le build)
- Lance Vite dev server pour le frontend
- Compile et lance l'application Tauri
- Tauri démarre automatiquement le backend Java

**Technologies :**
- Tauri v2 (Rust)
- Backend Java embarqué
- Port dynamique pour le backend

**Fenêtre de l'app :**
- Un écran de chargement apparaît
- Attend que le backend soit prêt (max 30s)
- Affiche l'interface une fois prêt

---

## Scripts de Build

Ces scripts compilent les applications pour la production.

### `build-backend.sh` - Build le JAR Spring Boot

**Usage :**
```bash
./scripts/build-backend.sh
```

**Sortie :**
- `backend/target/automates-backend.jar` (~19 MB)

**Tests :**
- Les tests sont skippés avec `-DskipTests`
- Pour inclure les tests : `cd backend && mvn clean package`

**Pour exécuter le JAR :**
```bash
java -jar backend/target/automates-backend.jar
```

---

### `build-frontend.sh` - Build le frontend SvelteKit

**Usage :**
```bash
./scripts/build-frontend.sh
```

**Sortie :**
- `frontend/build/` - Dossier avec les fichiers statiques

**Pour prévisualiser :**
```bash
cd frontend && npm run preview
```

**Déploiement :**
- Servir le dossier `build/` avec nginx, apache, ou autre
- Configurer le proxy vers le backend

---

### `build-web.sh` - Build Backend + Frontend

**Usage :**
```bash
./scripts/build-web.sh
```

**Appelle séquentiellement :**
1. `build-backend.sh`
2. `build-frontend.sh`

**Fichiers de déploiement :**
- `backend/target/automates-backend.jar`
- `frontend/build/`

---

### `build-desktop.sh` - Build l'application Desktop Tauri

**Usage :**
```bash
./scripts/build-desktop.sh
```

**Étapes :**
1. Build le backend JAR
2. Copie le JAR dans `desktop/src-tauri/resources/`
3. Build le frontend
4. Build Tauri (génère les installateurs)

**Sortie :**
```
desktop/src-tauri/target/release/bundle/
├── deb/              # Package Debian (.deb)
├── appimage/         # AppImage Linux
└── msi/              # Windows Installer
```

**Taille approximative :**
- Linux : ~30-35 MB (JAR + binaire Rust)
- Windows : ~25-30 MB

**Installation :**
```bash
# Debian/Ubuntu
sudo dpkg -i desktop/src-tauri/target/release/bundle/deb/*.deb

# AppImage
chmod +x *.AppImage && ./automates-editor*.AppImage
```

---

### `build-mobile-android.sh` - Build Android APK

**Usage :**
```bash
./scripts/build-mobile-android.sh
```

**Prérequis :**
- Android Studio installé
- Phase 4 (Capacitor) complétée

**Ce qu'il fait :**
1. Build le frontend avec `VITE_API_URL` pour production
2. Sync Capacitor
3. Ouvre Android Studio
4. Guide l'utilisateur pour générer l'APK signé

**Sortie :**
- `mobile/android/app/build/outputs/apk/release/`

---

### `build-mobile-ios.sh` - Build iOS IPA

**Usage :**
```bash
./scripts/build-mobile-ios.sh
```

**Prérequis :**
- macOS uniquement
- Xcode installé
- Phase 4 (Capacitor) complétée

**Ce qu'il fait :**
1. Build le frontend avec `VITE_API_URL` pour production
2. Sync Capacitor
3. Ouvre Xcode
4. Guide l'utilisateur pour archiver et distribuer

**Sortie :**
- Archive Xcode dans `~/Library/Developer/Xcode/Archives/`

---

### `build-all.sh` - Build TOUTES les applications

**Usage :**
```bash
./scripts/build-all.sh
```

**Appelle :**
1. `build-backend.sh`
2. `build-frontend.sh`
3. `build-desktop.sh`

**Note :**
- Mobile nécessite des étapes manuelles (non automatisé)

**Affiche un résumé :**
- Succès/échec de chaque build
- Temps total
- Emplacements des artifacts

**Sortie en cas d'erreur :**
- Exit code 1 si au moins un build échoue

---

## Scripts Utilitaires

### `clean.sh` - Nettoie tous les builds

**Usage :**
```bash
./scripts/clean.sh
```

**Ce qu'il supprime :**
- `backend/target/` - Maven build
- `frontend/build/` - SvelteKit build
- `frontend/.svelte-kit/` - Cache SvelteKit
- `desktop/src-tauri/target/` - Rust build
- `desktop/src-tauri/resources/backend.jar` - JAR embarqué
- `mobile/android/app/build/` - Android build
- `mobile/ios/App/build/` - iOS build

**Option :**
- Supprime aussi les `node_modules` (décommentez dans le script)

**Quand l'utiliser :**
- Après plusieurs builds ratés
- Pour libérer de l'espace disque
- Avant un rebuild complet

---

### `test.sh` - Lance tous les tests

**Usage :**
```bash
./scripts/test.sh
```

**Tests exécutés :**
1. Backend : `mvn test`
2. Frontend : `npm test` (si configuré)
3. Desktop : `cargo test` (si configuré)

**Sortie :**
- Résumé : nombre de suites qui ont échoué
- Exit code 1 si au moins un test échoue

---

## Structure du Projet

```
Automates/
├── backend/              # Spring Boot API
│   ├── src/
│   ├── pom.xml
│   └── target/          # Build output
│
├── frontend/            # SvelteKit Web App
│   ├── src/
│   ├── package.json
│   └── build/           # Build output
│
├── desktop/             # Tauri Desktop App
│   ├── src-tauri/       # Rust code
│   │   ├── src/
│   │   ├── Cargo.toml
│   │   ├── resources/   # Embedded resources
│   │   └── target/      # Build output
│   └── package.json
│
├── mobile/              # Capacitor Mobile Apps (Phase 4)
│   ├── android/
│   ├── ios/
│   └── capacitor.config.ts
│
├── scripts/             # 👈 Vous êtes ici
│   ├── setup.sh
│   ├── dev-*.sh
│   ├── build-*.sh
│   ├── clean.sh
│   ├── test.sh
│   └── README.md
│
└── docs/                # Documentation
    ├── PHASES-RESTANTES.md
    └── PHASE3-COMPLETE.md
```

---

## Workflow de Développement Recommandé

### Première fois

```bash
# 1. Setup initial
./scripts/setup.sh

# 2. Lancer l'app web
./scripts/dev-web.sh

# 3. Ou lancer l'app desktop
./scripts/dev-desktop.sh
```

### Développement quotidien

```bash
# Option A : Deux terminaux manuels
Terminal 1: ./scripts/dev-backend.sh
Terminal 2: ./scripts/dev-frontend.sh

# Option B : Automatique
./scripts/dev-web.sh

# Option C : Desktop
./scripts/dev-desktop.sh
```

### Avant un commit

```bash
# Tester
./scripts/test.sh

# Si des tests échouent, corriger puis relancer
```

### Build pour production

```bash
# Build tout
./scripts/build-all.sh

# Ou individuellement
./scripts/build-web.sh      # Pour déploiement web
./scripts/build-desktop.sh  # Pour distribution desktop
```

### Nettoyage

```bash
# Nettoyer tous les builds
./scripts/clean.sh

# Puis rebuild
./scripts/build-all.sh
```

---

## Variables d'Environnement

### Frontend

- `VITE_API_URL` : URL de l'API backend
  - Dev web : Non nécessaire (proxy Vite)
  - Prod web : `https://api.yourdomain.com`
  - Desktop : Détecté automatiquement par Tauri
  - Mobile : Configuré dans `capacitor.config.ts`

**Exemple :**
```bash
VITE_API_URL=https://api.production.com ./scripts/build-frontend.sh
```

### Backend

- `server.port` : Port du serveur (défaut : 8080)
- `spring.profiles.active` : Profil Spring (dev, prod, desktop)

**Exemple :**
```bash
java -jar backend.jar --server.port=9000 --spring.profiles.active=prod
```

---

## Troubleshooting

### "Command not found: cargo"

**Problème :** Rust n'est pas dans le PATH

**Solution :**
```bash
source $HOME/.cargo/env
```

Ou ajoutez à votre `~/.bashrc` :
```bash
export PATH="$HOME/.cargo/bin:$PATH"
```

---

### "Backend JAR not found"

**Problème :** Le JAR n'a pas été copié dans les ressources Tauri

**Solution :**
```bash
cd backend
mvn package -DskipTests
cp target/automates-backend.jar ../desktop/src-tauri/resources/backend.jar
```

---

### "Port 8080 already in use"

**Problème :** Le backend est déjà lancé ou un autre service utilise le port

**Solution :**
```bash
# Trouver le processus
lsof -i :8080

# Tuer le processus
kill -9 <PID>

# Ou utiliser un autre port
cd backend
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

---

### "npm install fails"

**Problème :** Cache npm corrompu

**Solution :**
```bash
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

---

## Scripts Avancés

### Build avec profil spécifique

```bash
# Backend avec profil prod
cd backend
mvn clean package -Pproduction

# Frontend avec URL API personnalisée
cd frontend
VITE_API_URL=https://api.myserver.com npm run build
```

### Build multi-plateforme Desktop

```bash
# Linux seulement
cd desktop
npm run tauri build -- --bundles deb,appimage

# Windows cross-compile (depuis Linux)
# Nécessite mingw-w64
npm run tauri build -- --target x86_64-pc-windows-gnu
```

---

## Contributions

Pour ajouter un nouveau script :

1. Créer le fichier dans `scripts/`
2. Ajouter l'en-tête avec couleurs
3. Rendre exécutable : `chmod +x scripts/mon-script.sh`
4. Documenter dans ce README
5. Tester sur une installation propre

---

## Support

Pour toute question sur les scripts :
- Consulter ce README
- Vérifier `docs/PHASES-RESTANTES.md`
- Lire les commentaires dans les scripts

**Bonne chance avec le développement ! 🚀**

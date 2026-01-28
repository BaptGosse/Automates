# Application Desktop Tauri - Automates

Application desktop native pour l'éditeur d'automates, construite avec Tauri v2 et Rust. Cette application embarque le backend Java Spring Boot pour fonctionner complètement offline.

## Architecture

```
desktop/
├── src/              # Code Svelte (non utilisé, on utilise ../frontend)
├── src-tauri/        # Code Rust Tauri
│   ├── src/
│   │   ├── lib.rs    # Code principal : démarrage backend Java
│   │   └── main.rs
│   ├── resources/    # Ressources embarquées
│   │   └── backend.jar   # JAR Spring Boot (copié lors du build)
│   ├── Cargo.toml
│   └── tauri.conf.json   # Configuration Tauri
└── package.json
```

## Prérequis

- **Rust 1.70+** (installé via \`rustup\`)
- **Node.js 18+**
- **Java 21+** (pour exécuter le backend)
- **Maven** (pour builder le backend)

## Développement

### 1. Préparer le backend JAR

Avant de lancer l'application desktop, il faut d'abord compiler le backend Java et le copier dans les ressources Tauri :

\`\`\`bash
# Depuis la racine du projet
cd backend
mvn clean package -DskipTests

# Copier le JAR dans les ressources Tauri
cp target/automates-backend.jar ../desktop/src-tauri/resources/backend.jar
\`\`\`

### 2. Lancer en mode développement

\`\`\`bash
cd desktop
npm run tauri dev
\`\`\`

Cela va :
1. Lancer le serveur de développement frontend (Vite) sur http://localhost:5173
2. Compiler et lancer l'application Tauri
3. L'application Tauri va automatiquement démarrer le backend Java sur un port dynamique
4. Afficher un écran de chargement pendant le démarrage du backend
5. Une fois prêt, afficher l'interface

## Build Production

Utilise le script automatisé :

\`\`\`bash
# Depuis la racine du projet
./scripts/build-desktop.sh
\`\`\`

### Fichiers de sortie

\`\`\`
desktop/src-tauri/target/release/bundle/
├── deb/              # Package Debian (.deb)
├── appimage/         # AppImage Linux
└── msi/              # Windows Installer
\`\`\`

## Test Rapide

Pour tester rapidement que tout fonctionne :

1. **Préparer le JAR** (une seule fois) :
   \`\`\`bash
   cd backend && mvn package -DskipTests && cp target/automates-backend.jar ../desktop/src-tauri/resources/backend.jar && cd ../desktop
   \`\`\`

2. **Lancer l'app** :
   \`\`\`bash
   npm run tauri dev
   \`\`\`

## Troubleshooting

### Erreur "java command not found"
Installer Java 21+ : \`sudo apt install openjdk-21-jdk\`

### Erreur "Backend n'a pas pu démarrer"
Vérifier que le JAR existe : \`ls -lh desktop/src-tauri/resources/backend.jar\`

## Ressources

- [Tauri Documentation](https://tauri.app/v2/)
- [Tauri API Reference](https://tauri.app/v2/reference/javascript/api/)

# Éditeur d'Automates - Application Fullstack

Application moderne d'édition d'automates finis avec architecture Spring Boot + Svelte.

## 🏗️ Architecture

```
┌─────────────────────────────────────┐
│     FRONTEND SVELTE                 │
│  (Interface web interactive)        │
└──────────────┬──────────────────────┘
               │ API REST
               ▼
┌─────────────────────────────────────┐
│     BACKEND SPRING BOOT             │
│  (Logique métier + analyse)         │
└─────────────────────────────────────┘
```

## 📁 Structure du Projet

```
automates/
├── backend/                    # Spring Boot API (✅ Terminé)
│   ├── src/main/java/fr/baptgosse/automates/
│   │   ├── model/             # Automaton, State, Transition
│   │   ├── service/           # AutomatonService, AnalysisService
│   │   ├── controller/        # AutomatonController (API REST)
│   │   └── config/            # Configuration CORS
│   └── pom.xml
│
├── frontend/                   # SvelteKit (✅ Terminé)
│   ├── src/
│   │   ├── lib/
│   │   │   ├── components/    # Canvas, Table, InfoPanel, Toolbar
│   │   │   ├── stores/        # Stores Svelte
│   │   │   ├── api/           # Client API
│   │   │   └── types/         # Types TypeScript
│   │   └── routes/
│   │       └── +page.svelte   # Page principale
│   └── package.json
│
├── desktop/                    # Tauri (⏳ À faire - Phase 3)
│   └── (à créer)
│
├── mobile/                     # Capacitor (⏳ À faire - Phase 4)
│   └── (à créer)
│
├── scripts/                    # Scripts utiles
│   ├── start-dev.sh           # Démarrage rapide dev
│   └── build-all.sh           # Build complet
│
├── docs/                       # Documentation
│   ├── BRAINSTORMING.md       # Brainstorming initial
│   └── PHASES-RESTANTES.md    # Plan détaillé phases 3-4
│
├── legacy-javafx/             # Ancien projet JavaFX
│   └── (archivé)
│
└── README.md
```

## 🎯 Scripts Rapides

### Démarrage Développement
```bash
# Option 1: Script automatique (lance backend + frontend)
./scripts/start-dev.sh

# Option 2: Manuel (2 terminaux)
# Terminal 1: Backend
cd backend && mvn spring-boot:run

# Terminal 2: Frontend
cd frontend && npm run dev
```

### Build Production
```bash
./scripts/build-all.sh
```

## 🚀 Démarrage Rapide

### Prérequis

- **Java 21+** (ou Java 17+)
- **Maven 3.8+**
- **Node.js 18+** et **npm**

### 1. Démarrer le Backend

```bash
cd backend
mvn spring-boot:run
```

Le backend démarre sur **http://localhost:8080**

### 2. Démarrer le Frontend (dans un autre terminal)

```bash
cd frontend
npm run dev
```

Le frontend démarre sur **http://localhost:5173**

### 3. Ouvrir l'Application

Ouvrez votre navigateur à l'adresse : **http://localhost:5173**

## 🛠️ Commandes Utiles

### Backend

```bash
# Build JAR standalone
cd backend
mvn clean package

# Exécuter le JAR
java -jar target/automates-backend.jar

# Build avec tests
mvn clean package
```

### Frontend

```bash
# Développement
cd frontend
npm run dev

# Build production
npm run build

# Prévisualiser le build
npm run preview
```

## 📡 API REST Endpoints

### Automates
- `POST /api/automaton` - Créer un nouvel automate
- `GET /api/automaton/{id}` - Récupérer un automate
- `PUT /api/automaton/{id}` - Mettre à jour un automate
- `DELETE /api/automaton/{id}` - Supprimer un automate

### États
- `POST /api/automaton/{id}/state` - Ajouter un état
- `DELETE /api/automaton/{id}/state/{stateId}` - Supprimer un état
- `PUT /api/automaton/{id}/state/{stateId}` - Modifier un état

### Transitions
- `POST /api/automaton/{id}/transition` - Ajouter une transition
- `DELETE /api/automaton/{id}/transition/{transitionId}` - Supprimer une transition

### Analyse
- `GET /api/automaton/{id}/table` - Récupérer la table de transitions
- `GET /api/automaton/{id}/info` - Récupérer l'analyse complète

## ✨ Fonctionnalités Implémentées

### Édition Graphique
- ✅ Ajout d'états par clic (génération automatique de labels q0, q1...)
- ✅ Création de transitions entre états
- ✅ Support des self-loops (transitions d'un état vers lui-même)
- ✅ Transitions bidirectionnelles (courbes de Bézier)
- ✅ Sélection d'états
- ✅ Suppression d'éléments
- ✅ Définition d'états initiaux et acceptants

### Visualisation
- ✅ Canvas SVG interactif
- ✅ Rendu académique (cercles, flèches, double cercles)
- ✅ Table de transitions dynamique
- ✅ Panneau d'informations avec analyse en temps réel

### Analyse
- ✅ Détection de déterminisme (AFD vs AFN)
- ✅ Détection de complétude
- ✅ Calcul automatique de l'alphabet
- ✅ Génération d'expressions régulières (cas simples)
- ✅ Description du langage reconnu

## 🎯 Utilisation

1. **Sélectionner l'outil** : Cliquez sur "Sélection", "Ajouter État" ou "Ajouter Transition"

2. **Créer des états** :
   - Cliquez sur "Ajouter État"
   - Cliquez n'importe où sur le canvas
   - L'état est créé automatiquement avec un label (q0, q1...)

3. **Créer des transitions** :
   - Cliquez sur "Ajouter Transition"
   - Cliquez sur l'état source
   - Cliquez sur l'état cible
   - Entrez le symbole de la transition

4. **Définir les propriétés** :
   - Sélectionnez un état avec l'outil "Sélection"
   - Cliquez sur "Définir Initial" ou "Définir Acceptant"

5. **Consulter l'analyse** :
   - La table de transitions et le panneau d'informations se mettent à jour automatiquement
   - Visualisez le type d'automate (AFD/AFN), la complétude, etc.

## 🔧 Technologies Utilisées

### Backend
- **Java 21**
- **Spring Boot 3.2.2**
- **Maven 3.8+**

### Frontend
- **SvelteKit 2.0**
- **TypeScript 5.0**
- **Vite 5.0**
- **Axios 1.6** (client HTTP)

## 📝 Développement

### Structure des Composants Svelte

- **AutomatonCanvas.svelte** : Canvas SVG principal avec logique d'édition
- **Toolbar.svelte** : Barre d'outils avec tous les boutons
- **TransitionTable.svelte** : Table de transitions δ(état, symbole) → cible
- **InfoPanel.svelte** : Panneau d'informations et d'analyse

### Stores Svelte

- `automaton` : Store principal contenant l'état de l'automate
- `selectedState` : État actuellement sélectionné
- `selectedTransition` : Transition actuellement sélectionnée
- `currentTool` : Outil actuellement actif
- `sessionId` : ID de session backend

### Services Backend

- **AutomatonService** : CRUD des automates, états et transitions
- **AnalysisService** : Analyse de déterminisme, complétude, génération regex

## 🐛 Résolution de Problèmes

### Le backend ne démarre pas
- Vérifiez que Java 21 est installé : `java --version`
- Vérifiez qu'aucun autre processus n'utilise le port 8080

### Le frontend ne se connecte pas au backend
- Vérifiez que le backend est bien démarré sur le port 8080
- Vérifiez la configuration du proxy dans `frontend/vite.config.ts`

### Erreurs de compilation TypeScript
- Supprimez `node_modules` et `package-lock.json`
- Réinstallez : `npm install`

## 🚧 Prochaines Étapes

Phase 3 : Desktop (Tauri)
- [ ] Setup Tauri
- [ ] Backend Java embarqué
- [ ] Build desktop

Phase 4 : Mobile (Capacitor)
- [ ] Setup Capacitor
- [ ] Adaptations UI mobile
- [ ] Build Android/iOS

## 📄 Licence

Projet éducatif développé dans le cadre du cours R4.12 sur les automates et langages formels.

## 👨‍💻 Auteur

Baptiste Gosselin

## 📖 Références

- Cours : `/docs/courses/*.pdf`
- Exercices : `/docs/courses/ex_R4_12_enonce.pdf`

## 🎓 Contexte

Projet développé en parallèle du cours de Mathématiques R4.12 sur les automates et langages formels, et basé sur le cours de M. Secouard, professeur à l'IUT Grand Ouest Normandie (https://iut-grand-ouest-normandie.unicaen.fr/), une composante de l'Université de Caen Normandie (https://www.unicaen.fr/). C'est ainsi que je remercie M. Secouard pour ses cours, qui me permettent de créer cette application. Je précise que les cours ne sont pas accessibles ici, sur ce repo GitHub, tant que je n'ai pas l'accord de M. Secouard, et par respect pour son travail.

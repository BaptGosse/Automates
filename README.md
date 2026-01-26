# Éditeur d'Automates

Application JavaFX pour créer, visualiser et manipuler des automates finis.

## 🚀 Lancement

```bash
# Compiler et lancer
mvn clean javafx:run
```

## ✨ Fonctionnalités Actuelles

### Édition Graphique
- ✅ **Ajout d'états** : Clic sur le canvas
- ✅ **Création de transitions** : Sélection de deux états + symbole
- ✅ **Boucles (self-loops)** : Transition d'un état vers lui-même
- ✅ **Suppression d'éléments** : Sélection + bouton Supprimer
- ✅ **Définir état initial** : Flèche entrante longue
- ✅ **Définir états acceptants** : Double cercle

### Visualisation
- ✅ **Style académique** : Rendu conforme aux exercices de cours
- ✅ **Table de transitions** : Mise à jour automatique
- ✅ **Panneau d'informations** :
  - Type d'automate (AFD/AFN)
  - Alphabet Σ
  - Nombre d'états et transitions
  - État initial et états acceptants
  - Déterminisme et complétude
  - Description du langage

### Détection Automatique
- ✅ **Automate déterministe** : Vérifie les conditions AFD
- ✅ **Automate complet** : Vérifie toutes les transitions
- ✅ **ε-transitions** : Détection automatique

## 🎨 Interface

```
┌────────────────────────────────────────────────────────┐
│ Menu Bar                                               │
├────────────────────────────────────────────────────────┤
│ [Sélection] [Ajouter État] [Ajouter Transition] ...   │
├──────────────────────────┬─────────────────────────────┤
│                          │ ┌───────────────────────┐   │
│    Canvas d'Automate     │ │ Table de Transitions  │   │
│                          │ │ ───────────────────── │   │
│      ┌───┐   a   ┌───┐  │ │   | a | b |           │   │
│   →  │ 1 │ ───→  │ 2 │  │ │ 1 | 2 | 1 |           │   │
│      └───┘       └───┘  │ │ 2 | - | 3 |           │   │
│                          │ └───────────────────────┘   │
│                          │ ┌───────────────────────┐   │
│                          │ │ Informations          │   │
│                          │ │ ───────────────────── │   │
│                          │ │ Type: AFD             │   │
│                          │ │ Alphabet: {a, b}      │   │
│                          │ │ États: 2              │   │
│                          │ └───────────────────────┘   │
└──────────────────────────┴─────────────────────────────┘
```

## 🎯 Utilisation

### Créer un Automate

1. **Ajouter des états** :
   - Cliquez sur "Ajouter État"
   - Cliquez sur le canvas où vous voulez placer l'état
   - L'état est automatiquement nommé (q0, q1, q2...)

2. **Créer des transitions** :
   - Cliquez sur "Ajouter Transition"
   - Cliquez sur l'état de départ
   - Cliquez sur l'état d'arrivée
   - Entrez le symbole dans la boîte de dialogue

3. **Définir l'état initial** :
   - Sélectionnez un état avec l'outil "Sélection"
   - Cliquez sur "Définir Initial"
   - Une flèche apparaît à gauche de l'état

4. **Définir un état acceptant** :
   - Sélectionnez un état
   - Cliquez sur "Définir Acceptant"
   - Un double cercle apparaît autour de l'état

### Consulter les Informations

- **Table de transitions** : Onglet "Table de Transitions"
  - Format : δ(état, symbole) = état cible
  - Légende : `→ q0` = initial, `q1 *` = acceptant

- **Informations détaillées** : Onglet "Informations"
  - Type d'automate (déterministe ou non)
  - Complétude
  - Alphabet utilisé
  - États initiaux et acceptants

## 📝 Légende

### États
- `○` : État simple
- `⊙` : État acceptant (double cercle)
- `→ ○` : État initial (flèche entrante)

### Transitions
- `─a→` : Transition simple avec symbole 'a'
- `↻a` : Boucle sur un état
- Courbes : Transitions bidirectionnelles

## 🔧 Technologies

- **Java 17**
- **JavaFX 21**
- **Maven 3.8+**
- **Lombok** (génération de code)

## 📚 Prochaines Fonctionnalités

- [ ] Export en image (PNG, SVG, PDF)
- [ ] Sauvegarde/Chargement d'automates (JSON)
- [ ] Conversion en expression régulière
- [ ] Déterminisation (AFN → AFD)
- [ ] Minimisation d'automate
- [ ] Vérification d'équivalence
- [ ] Test de mots
- [ ] Animation de l'exécution
- [ ] Drag & drop des états

## 📖 Références

- Cours : `/src/courses/*.pdf`
- Exercices : `/src/courses/ex_R4_12_enonce.pdf`

## 🎓 Contexte

Projet développé dans le cadre du cours de Mathématiques R4-A-12 sur les automates et langages formels.

# AMDL Quick Start Guide

## Introduction rapide

AMDL est un langage pour décrire des automates de manière lisible et concise.

## Exemple minimal

```amdl
Automaton "Mon premier automate" {
  alphabet: [a, b]
  
  State q0 [initial]
  State q1 [accepting]
  
  q0 -> q1 [a]
  q1 -> q0 [b]
}
```

## Structure de base

### 1. Déclarer un automate

```amdl
Automaton "Nom" {
  // contenu
}
```

### 2. Définir l'alphabet

```amdl
alphabet: [a, b, c]
```

### 3. Créer des états

```amdl
State q0 [initial]           // État initial
State q1 [accepting]         // État acceptant
State q2 [initial, accepting] // Les deux
State q3                     // État normal
```

### 4. Ajouter des transitions

```amdl
q0 -> q1 [a]      // De q0 vers q1 sur 'a'
q1 -> q1 [b]      // Boucle sur q1
q2 -> q3 [a, b]   // Multiple symboles (NFA)
```

## Exemple complet

```amdl
// Automate reconnaissant (ab)*
Automaton "Répétition de ab" {
  alphabet: [a, b]
  
  // États
  State q0 [initial, accepting] at (100, 200)
  State q1 at (300, 200)
  
  // Transitions
  q0 -> q1 [a]  // Commence par 'a'
  q1 -> q0 [b]  // Complète avec 'b'
}
```

## Conversion JSON

Le parser AMDL convertit automatiquement en JSON compatible avec l'API :

**AMDL** → **JSON**

```amdl
Automaton "Simple" {
  alphabet: [a, b]
  State q0 [initial]
  State q1 [accepting]
  q0 -> q1 [a]
}
```

```json
{
  "name": "Simple",
  "states": [
    {"id": "q0", "initial": true, "accepting": false},
    {"id": "q1", "initial": false, "accepting": true}
  ],
  "transitions": [
    {"from": "q0", "to": "q1", "symbol": "a"}
  ]
}
```

## Utilisation dans l'application

### Importer un fichier AMDL

1. Créer un fichier `.amdl`
2. Aller dans l'application
3. Cliquer sur "Import" → "AMDL File"
4. Sélectionner le fichier

### Exporter en AMDL

1. Créer votre automate dans l'interface
2. Cliquer sur "Export" → "AMDL Format"
3. Télécharger le fichier `.amdl`

## Prochaines étapes

- Voir la [spécification complète](AMDL-SPEC.md)
- Explorer les [exemples](examples.amdl)
- Contribuer au [parser](https://github.com/...)


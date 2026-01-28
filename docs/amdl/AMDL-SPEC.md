# AMDL - Automaton Model Description Language

**Version:** 1.0.0  
**Inspiré par:** DBML (Database Markup Language)

## Vue d'ensemble

AMDL est un langage de description d'automates finis, conçu pour être :
- **Lisible** : Syntaxe claire et concise
- **Expressif** : Capture toutes les propriétés d'un automate
- **Portable** : Facilement convertible en JSON
- **Éditable** : Peut être écrit à la main ou généré

---

## Syntaxe

### 1. Déclaration d'un automate

```amdl
Automaton "Nom de l'automate" {
  // Contenu de l'automate
}
```

**Attributs optionnels :**
```amdl
Automaton "Mon Automate" [
  type: "DFA",           // DFA, NFA, epsilon-NFA (défaut: DFA)
  description: "...",    // Description textuelle
  author: "...",         // Auteur
  version: "1.0.0"       // Version
] {
  // ...
}
```

---

### 2. Alphabet

Définit les symboles acceptés par l'automate.

```amdl
alphabet: [a, b, c, 0, 1]
```

Ou avec epsilon :
```amdl
alphabet: [a, b, ε]  // ou epsilon, eps, λ
```

---

### 3. États (States)

#### Syntaxe basique
```amdl
State nom_etat
```

#### Avec propriétés
```amdl
State q0 {
  initial: true          // État initial (défaut: false)
  accepting: true        // État acceptant (défaut: false)
  position: (x, y)       // Position graphique (optionnel)
  label: "État 0"        // Label affiché (optionnel)
}
```

#### Syntaxe raccourcie
```amdl
State q0 [initial]              // État initial
State q1 [accepting]            // État acceptant
State q2 [initial, accepting]   // Les deux
State q3 at (100, 200)          // Avec position
```

---

### 4. Transitions

#### Syntaxe basique
```amdl
from_state -> to_state [symbol]
```

#### Exemples
```amdl
q0 -> q1 [a]           // Transition simple
q1 -> q1 [b]           // Boucle
q2 -> q3 [ε]           // Transition epsilon
```

#### Multiples symboles (NFA)
```amdl
q0 -> q1 [a, b]        // Transition sur 'a' OU 'b'
```

#### Avec propriétés
```amdl
q0 -> q1 [a] {
  label: "a"           // Label personnalisé
  color: "#ff0000"     // Couleur (optionnel)
  curved: 0.2          // Courbure (optionnel)
}
```

---

### 5. Commentaires

```amdl
// Commentaire sur une ligne

/* 
   Commentaire
   sur plusieurs
   lignes
*/
```

---

## Exemple complet

```amdl
/*
 * Automate reconnaissant les mots contenant "ab"
 * Alphabet : {a, b}
 */

Automaton "Contient ab" [
  type: "DFA",
  description: "Reconnaît les mots contenant la sous-chaîne 'ab'",
  author: "Baptiste Gosselin"
] {
  
  // Définition de l'alphabet
  alphabet: [a, b]
  
  // États
  State q0 [initial] at (100, 200) {
    label: "Start"
  }
  
  State q1 at (300, 200) {
    label: "A vu"
  }
  
  State q2 [accepting] at (500, 200) {
    label: "AB vu"
  }
  
  // Transitions
  q0 -> q0 [b]      // Reste en q0 si 'b'
  q0 -> q1 [a]      // Va en q1 si 'a'
  
  q1 -> q2 [b]      // Va en q2 si 'b' (ab trouvé !)
  q1 -> q1 [a]      // Reste en q1 si 'a'
  
  q2 -> q2 [a, b]   // Reste en q2 pour tout (acceptant)
}
```

---

## Grammaire BNF

```bnf
<automaton>    ::= "Automaton" <string> <attributes>? "{" <body> "}"

<attributes>   ::= "[" <attr_list> "]"
<attr_list>    ::= <attribute> ("," <attribute>)*
<attribute>    ::= <identifier> ":" <value>

<body>         ::= <statement>*

<statement>    ::= <alphabet_def>
                 | <state_def>
                 | <transition_def>
                 | <comment>

<alphabet_def> ::= "alphabet" ":" "[" <symbol_list> "]"
<symbol_list>  ::= <symbol> ("," <symbol>)*
<symbol>       ::= <identifier> | "ε" | "epsilon" | "eps" | "λ"

<state_def>    ::= "State" <identifier> <state_attrs>? <state_body>?
<state_attrs>  ::= "[" <state_flag> ("," <state_flag>)* "]"
                 | "at" "(" <number> "," <number> ")"
<state_flag>   ::= "initial" | "accepting"
<state_body>   ::= "{" <state_prop>* "}"
<state_prop>   ::= <identifier> ":" <value>

<transition_def> ::= <identifier> "->" <identifier> "[" <symbol_list> "]" <trans_body>?
<trans_body>     ::= "{" <trans_prop>* "}"
<trans_prop>     ::= <identifier> ":" <value>

<value>        ::= <string> | <number> | <boolean> | <array>
<string>       ::= '"' [^"]* '"'
<number>       ::= [0-9]+ ("." [0-9]+)?
<boolean>      ::= "true" | "false"
<array>        ::= "[" <value> ("," <value>)* "]"
<identifier>   ::= [a-zA-Z_][a-zA-Z0-9_]*

<comment>      ::= "//" [^\n]* | "/*" .* "*/"
```

---

## Équivalence JSON

Chaque fichier AMDL peut être converti en JSON :

**AMDL :**
```amdl
Automaton "Simple" {
  alphabet: [a, b]
  
  State q0 [initial]
  State q1 [accepting]
  
  q0 -> q1 [a]
}
```

**JSON équivalent :**
```json
{
  "name": "Simple",
  "type": "DFA",
  "alphabet": ["a", "b"],
  "states": [
    {
      "id": "q0",
      "initial": true,
      "accepting": false,
      "position": null
    },
    {
      "id": "q1",
      "initial": false,
      "accepting": true,
      "position": null
    }
  ],
  "transitions": [
    {
      "from": "q0",
      "to": "q1",
      "symbols": ["a"]
    }
  ]
}
```

---

## Extensions futures

- **Groupes d'états** : `Group "Acceptants" { q1, q2, q3 }`
- **Macros de transitions** : `q0 => [q1, q2, q3] on [a]`
- **Expressions régulières** : `regex: (a|b)*ab(a|b)*`
- **Tests** : `test "aab" should accept`
- **Minimisation** : `minimize: true`

---

## Outils

- **Parser** : Java + TypeScript
- **Validateur** : Vérifie la cohérence de l'automate
- **Formateur** : Pretty-print AMDL
- **Convertisseur** : AMDL ↔ JSON
- **Générateur** : Génère AMDL depuis un automate existant

---

## Licence

AMDL est open-source sous licence MIT.

**Auteur :** Baptiste Gosselin  
**Année :** 2026

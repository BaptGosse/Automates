package fr.baptgosse.automates.util;

import fr.baptgosse.automates.model.Automaton;
import fr.baptgosse.automates.model.State;
import fr.baptgosse.automates.model.Transition;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Génère une expression régulière à partir d'un automate.
 */
public class RegexGenerator {

    /**
     * Génère une expression régulière simple pour un automate.
     * Note: Ceci est une implémentation simplifiée qui détecte des patterns courants.
     * Pour une conversion complète, il faudrait implémenter l'algorithme de suppression d'états.
     */
    public static String generateRegex(Automaton automaton) {
        // Vérifications de base
        if (automaton.getStates().isEmpty()) {
            return "∅";
        }

        Optional<State> initialState = automaton.getInitialState();
        if (initialState.isEmpty()) {
            return "∅ (pas d'état initial)";
        }

        Set<State> acceptingStates = automaton.getAcceptingStates();
        if (acceptingStates.isEmpty()) {
            return "∅ (aucun état acceptant)";
        }

        // Cas simple : un seul état initial qui est aussi acceptant
        if (automaton.getStates().size() == 1 && initialState.get().isAccepting()) {
            Set<Transition> selfLoops = automaton.getTransitionsFrom(initialState.get());
            if (selfLoops.isEmpty()) {
                return "ε";
            }

            Set<String> symbols = selfLoops.stream()
                    .filter(t -> t.getFrom().equals(t.getTo()))
                    .map(Transition::getSymbol)
                    .collect(Collectors.toSet());

            if (!symbols.isEmpty()) {
                String symbolsRegex = symbols.size() == 1
                    ? symbols.iterator().next()
                    : "(" + String.join("+", symbols) + ")";
                return symbolsRegex + "*";
            }
        }

        // Cas simple : chemin linéaire
        String linearRegex = tryLinearPath(automaton, initialState.get(), acceptingStates);
        if (linearRegex != null) {
            return linearRegex;
        }

        // Pour les cas plus complexes, on affiche une description
        return generateDescription(automaton);
    }

    /**
     * Essaie de détecter un chemin linéaire simple.
     */
    private static String tryLinearPath(Automaton automaton, State initial, Set<State> accepting) {
        // Tenter de trouver un chemin simple de l'état initial à un état acceptant
        StringBuilder regex = new StringBuilder();
        State current = initial;
        Set<State> visited = new HashSet<>();

        while (!accepting.contains(current)) {
            if (visited.contains(current)) {
                return null; // Il y a un cycle
            }
            visited.add(current);

            Set<Transition> outgoing = automaton.getTransitionsFrom(current);
            if (outgoing.size() != 1) {
                return null; // Pas un chemin linéaire
            }

            Transition trans = outgoing.iterator().next();
            if (!trans.getFrom().equals(current) || trans.getTo().equals(current)) {
                return null; // Boucle ou incohérence
            }

            regex.append(trans.getSymbol());
            current = trans.getTo();
        }

        // Vérifier s'il y a des boucles sur l'état acceptant
        final State finalState = current; // Variable finale pour le lambda
        Set<Transition> finalLoops = automaton.getTransitionsFrom(finalState).stream()
                .filter(t -> t.getTo().equals(finalState))
                .collect(Collectors.toSet());

        if (!finalLoops.isEmpty()) {
            Set<String> loopSymbols = finalLoops.stream()
                    .map(Transition::getSymbol)
                    .collect(Collectors.toSet());
            String loopRegex = loopSymbols.size() == 1
                ? loopSymbols.iterator().next()
                : "(" + String.join("+", loopSymbols) + ")";
            regex.append(loopRegex).append("*");
        }

        return regex.toString();
    }

    /**
     * Génère une description textuelle de l'automate.
     */
    private static String generateDescription(Automaton automaton) {
        StringBuilder desc = new StringBuilder();

        // Analyser les patterns
        boolean hasSelfLoops = automaton.getTransitions().stream()
                .anyMatch(t -> t.getFrom().equals(t.getTo()));

        Set<String> alphabet = automaton.getAlphabet();

        if (alphabet.isEmpty()) {
            desc.append("ε");
        } else if (alphabet.size() == 1) {
            String symbol = alphabet.iterator().next();
            desc.append("(langage sur '").append(symbol).append("')");
        } else {
            desc.append("(langage sur {").append(String.join(", ", alphabet)).append("})");
        }

        if (hasSelfLoops) {
            desc.append("*");
        }

        return desc.toString();
    }
}

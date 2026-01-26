package fr.baptgosse.automates.model;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Représente un automate fini.
 */
@Data
public class Automaton {
    private final Set<State> states;
    private final Set<Transition> transitions;
    private final Set<String> alphabet;
    private String name;

    public Automaton() {
        this.states = new HashSet<>();
        this.transitions = new HashSet<>();
        this.alphabet = new TreeSet<>();
        this.name = "Automate";
    }

    public Automaton(String name) {
        this();
        this.name = name;
    }

    /**
     * Ajoute un état à l'automate.
     */
    public void addState(State state) {
        states.add(state);
    }

    /**
     * Supprime un état et toutes ses transitions associées.
     */
    public void removeState(State state) {
        states.remove(state);
        transitions.removeIf(t -> t.getFrom().equals(state) || t.getTo().equals(state));
    }

    /**
     * Ajoute une transition à l'automate.
     */
    public void addTransition(Transition transition) {
        transitions.add(transition);
        if (!transition.isEpsilon() && !transition.getSymbol().isEmpty()) {
            alphabet.add(transition.getSymbol());
        }
    }

    /**
     * Supprime une transition.
     */
    public void removeTransition(Transition transition) {
        transitions.remove(transition);
        // Recalculer l'alphabet
        updateAlphabet();
    }

    /**
     * Met à jour l'alphabet en fonction des transitions.
     */
    private void updateAlphabet() {
        alphabet.clear();
        transitions.stream()
                .filter(t -> !t.isEpsilon() && !t.getSymbol().isEmpty())
                .forEach(t -> alphabet.add(t.getSymbol()));
    }

    /**
     * Retourne l'état initial (s'il existe).
     */
    public Optional<State> getInitialState() {
        return states.stream()
                .filter(State::isInitial)
                .findFirst();
    }

    /**
     * Retourne tous les états acceptants.
     */
    public Set<State> getAcceptingStates() {
        return states.stream()
                .filter(State::isAccepting)
                .collect(Collectors.toSet());
    }

    /**
     * Retourne les transitions depuis un état donné.
     */
    public Set<Transition> getTransitionsFrom(State state) {
        return transitions.stream()
                .filter(t -> t.getFrom().equals(state))
                .collect(Collectors.toSet());
    }

    /**
     * Retourne les transitions vers un état donné.
     */
    public Set<Transition> getTransitionsTo(State state) {
        return transitions.stream()
                .filter(t -> t.getTo().equals(state))
                .collect(Collectors.toSet());
    }

    /**
     * Trouve l'état à une position donnée.
     */
    public Optional<State> getStateAt(double x, double y) {
        return states.stream()
                .filter(s -> s.contains(x, y))
                .findFirst();
    }

    /**
     * Génère la table de transitions sous forme de Map.
     * Map<État, Map<Symbole, Set<État>>>
     */
    public Map<State, Map<String, Set<State>>> getTransitionTable() {
        Map<State, Map<String, Set<State>>> table = new LinkedHashMap<>();

        for (State state : states) {
            Map<String, Set<State>> symbolMap = new LinkedHashMap<>();

            for (String symbol : alphabet) {
                Set<State> targetStates = transitions.stream()
                        .filter(t -> t.getFrom().equals(state) && t.getSymbol().equals(symbol))
                        .map(Transition::getTo)
                        .collect(Collectors.toSet());
                symbolMap.put(symbol, targetStates);
            }

            table.put(state, symbolMap);
        }

        return table;
    }

    /**
     * Génère un nouvel identifiant pour un état.
     */
    public String generateStateLabel() {
        int counter = 0;
        while (true) {
            final String label = "q" + counter;
            if (states.stream().noneMatch(s -> label.equals(s.getLabel()))) {
                return label;
            }
            counter++;
        }
    }

    /**
     * Nettoie l'automate (supprime les états isolés, etc.).
     */
    public void clean() {
        // Supprimer les états sans transitions (sauf l'état initial)
        states.removeIf(s -> !s.isInitial() &&
                            getTransitionsFrom(s).isEmpty() &&
                            getTransitionsTo(s).isEmpty());
    }

    @Override
    public String toString() {
        return String.format("Automaton[name=%s, states=%d, transitions=%d, alphabet=%s]",
                name, states.size(), transitions.size(), alphabet);
    }
}

package fr.baptgosse.automates.service;

import fr.baptgosse.automates.dto.AutomatonInfo;
import fr.baptgosse.automates.model.Automaton;
import fr.baptgosse.automates.model.State;
import fr.baptgosse.automates.util.RegexGenerator;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Service d'analyse des propriétés d'un automate.
 * Logique extraite de AutomatonInfoPanel.java
 */
@Service
public class AnalysisService {

    /**
     * Analyse un automate et retourne ses informations.
     */
    public AutomatonInfo analyze(Automaton automaton) {
        boolean isDeterministic = isDeterministic(automaton);
        boolean isComplete = isComplete(automaton);

        String type = isDeterministic ? "AFD (Automate Fini Déterministe)" : "AFN (Automate Fini Non-Déterministe)";

        String initialState = automaton.getInitialState()
                .map(State::getLabel)
                .orElse(null);

        var acceptingStates = automaton.getAcceptingStates().stream()
                .map(State::getLabel)
                .collect(Collectors.toSet());

        String regex = RegexGenerator.generateRegex(automaton);
        String languageDesc = generateLanguageDescription(automaton);

        return new AutomatonInfo(
                automaton.getName(),
                type,
                isDeterministic,
                isComplete,
                automaton.getStates().size(),
                automaton.getTransitions().size(),
                automaton.getAlphabet(),
                initialState,
                acceptingStates,
                regex,
                languageDesc
        );
    }

    /**
     * Vérifie si l'automate est déterministe.
     * Logique extraite de AutomatonInfoPanel.java lignes 186-220
     */
    public boolean isDeterministic(Automaton automaton) {
        if (automaton.getStates().isEmpty()) {
            return true;
        }

        // Vérifier qu'il n'y a qu'un seul état initial
        long initialCount = automaton.getStates().stream()
                .filter(State::isInitial)
                .count();
        if (initialCount != 1) {
            return false;
        }

        // Vérifier qu'il n'y a pas d'ε-transitions
        boolean hasEpsilon = automaton.getTransitions().stream()
                .anyMatch(t -> t.isEpsilon());
        if (hasEpsilon) {
            return false;
        }

        // Vérifier qu'il n'y a pas deux transitions avec le même symbole depuis un état
        for (State state : automaton.getStates()) {
            var transitionsFromState = automaton.getTransitionsFrom(state);
            long distinctSymbols = transitionsFromState.stream()
                    .map(t -> t.getSymbol())
                    .distinct()
                    .count();

            if (distinctSymbols != transitionsFromState.size()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Vérifie si l'automate est complet.
     * Logique extraite de AutomatonInfoPanel.java lignes 225-243
     */
    public boolean isComplete(Automaton automaton) {
        if (!isDeterministic(automaton) || automaton.getAlphabet().isEmpty()) {
            return false;
        }

        // Pour chaque état, vérifier qu'il y a une transition pour chaque symbole
        for (State state : automaton.getStates()) {
            var transitionsFromState = automaton.getTransitionsFrom(state);
            var symbolsFromState = transitionsFromState.stream()
                    .map(t -> t.getSymbol())
                    .collect(Collectors.toSet());

            if (!symbolsFromState.containsAll(automaton.getAlphabet())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Génère une description textuelle du langage reconnu.
     */
    private String generateLanguageDescription(Automaton automaton) {
        if (automaton.getStates().isEmpty()) {
            return "Langage vide (∅)";
        }

        if (automaton.getInitialState().isEmpty()) {
            return "Pas d'état initial défini";
        }

        if (automaton.getAcceptingStates().isEmpty()) {
            return "Langage vide (aucun état acceptant)";
        }

        // Description basique
        StringBuilder desc = new StringBuilder("L(A) = { mots sur ");

        if (automaton.getAlphabet().isEmpty()) {
            desc.append("∅");
        } else {
            desc.append("{").append(String.join(", ", automaton.getAlphabet())).append("}");
        }

        desc.append(" reconnus par cet automate }");

        return desc.toString();
    }
}

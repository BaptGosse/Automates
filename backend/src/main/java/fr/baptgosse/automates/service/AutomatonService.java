package fr.baptgosse.automates.service;

import fr.baptgosse.automates.model.Automaton;
import fr.baptgosse.automates.model.State;
import fr.baptgosse.automates.model.Transition;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service de gestion des automates.
 * Gère les sessions en mémoire.
 */
@Service
public class AutomatonService {

    // Stockage en mémoire des automates par ID de session
    private final Map<String, Automaton> sessions = new ConcurrentHashMap<>();

    /**
     * Crée un nouvel automate.
     * @param name Nom de l'automate
     * @return ID de session de l'automate créé
     */
    public String createAutomaton(String name) {
        String sessionId = UUID.randomUUID().toString();
        Automaton automaton = new Automaton(name != null ? name : "Mon Automate");
        sessions.put(sessionId, automaton);
        return sessionId;
    }

    /**
     * Récupère un automate par son ID de session.
     */
    public Optional<Automaton> getAutomaton(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    /**
     * Met à jour un automate complet.
     */
    public void updateAutomaton(String sessionId, Automaton automaton) {
        sessions.put(sessionId, automaton);
    }

    /**
     * Supprime un automate.
     */
    public void deleteAutomaton(String sessionId) {
        sessions.remove(sessionId);
    }

    /**
     * Ajoute un état à un automate.
     */
    public State addState(String sessionId, double x, double y) {
        Automaton automaton = sessions.get(sessionId);
        if (automaton == null) {
            throw new IllegalArgumentException("Automate non trouvé: " + sessionId);
        }

        String label = automaton.generateStateLabel();
        State state = new State(label, x, y);
        automaton.addState(state);
        return state;
    }

    /**
     * Supprime un état.
     */
    public void removeState(String sessionId, String stateId) {
        Automaton automaton = sessions.get(sessionId);
        if (automaton == null) {
            throw new IllegalArgumentException("Automate non trouvé: " + sessionId);
        }

        State state = findStateById(automaton, stateId);
        if (state != null) {
            automaton.removeState(state);
        }
    }

    /**
     * Met à jour un état (position, propriétés).
     */
    public void updateState(String sessionId, String stateId, double x, double y,
                           Boolean initial, Boolean accepting) {
        Automaton automaton = sessions.get(sessionId);
        if (automaton == null) {
            throw new IllegalArgumentException("Automate non trouvé: " + sessionId);
        }

        State state = findStateById(automaton, stateId);
        if (state == null) {
            throw new IllegalArgumentException("État non trouvé: " + stateId);
        }

        state.setX(x);
        state.setY(y);
        if (initial != null) {
            // Si on définit cet état comme initial, retirer les autres
            if (initial) {
                automaton.getStates().forEach(s -> s.setInitial(false));
            }
            state.setInitial(initial);
        }
        if (accepting != null) {
            state.setAccepting(accepting);
        }
    }

    /**
     * Ajoute une transition.
     */
    public Transition addTransition(String sessionId, String fromId, String toId, String symbol) {
        Automaton automaton = sessions.get(sessionId);
        if (automaton == null) {
            throw new IllegalArgumentException("Automate non trouvé: " + sessionId);
        }

        State from = findStateById(automaton, fromId);
        State to = findStateById(automaton, toId);

        if (from == null || to == null) {
            throw new IllegalArgumentException("État source ou cible non trouvé");
        }

        Transition transition = new Transition(from, to, symbol);
        automaton.addTransition(transition);
        return transition;
    }

    /**
     * Supprime une transition.
     */
    public void removeTransition(String sessionId, String transitionId) {
        Automaton automaton = sessions.get(sessionId);
        if (automaton == null) {
            throw new IllegalArgumentException("Automate non trouvé: " + sessionId);
        }

        Transition transition = findTransitionById(automaton, transitionId);
        if (transition != null) {
            automaton.removeTransition(transition);
        }
    }

    /**
     * Trouve un état par son ID.
     */
    private State findStateById(Automaton automaton, String stateId) {
        return automaton.getStates().stream()
                .filter(s -> s.getId().equals(stateId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Trouve une transition par son ID.
     */
    private Transition findTransitionById(Automaton automaton, String transitionId) {
        return automaton.getTransitions().stream()
                .filter(t -> t.getId().equals(transitionId))
                .findFirst()
                .orElse(null);
    }
}

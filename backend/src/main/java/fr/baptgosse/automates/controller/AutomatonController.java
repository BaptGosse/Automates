package fr.baptgosse.automates.controller;

import fr.baptgosse.automates.dto.AutomatonInfo;
import fr.baptgosse.automates.model.Automaton;
import fr.baptgosse.automates.model.State;
import fr.baptgosse.automates.model.Transition;
import fr.baptgosse.automates.service.AnalysisService;
import fr.baptgosse.automates.service.AutomatonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller pour la gestion des automates.
 */
@RestController
@RequestMapping("/api/automaton")
@CrossOrigin(origins = "*") // À configurer plus finement en production
public class AutomatonController {

    @Autowired
    private AutomatonService automatonService;

    @Autowired
    private AnalysisService analysisService;

    /**
     * Crée un nouvel automate.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createAutomaton(@RequestBody(required = false) Map<String, String> body) {
        String name = body != null ? body.get("name") : null;
        String sessionId = automatonService.createAutomaton(name);
        return ResponseEntity.ok(Map.of("sessionId", sessionId));
    }

    /**
     * Récupère un automate par son ID de session.
     */
    @GetMapping("/{sessionId}")
    public ResponseEntity<Automaton> getAutomaton(@PathVariable String sessionId) {
        return automatonService.getAutomaton(sessionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Met à jour un automate complet.
     */
    @PutMapping("/{sessionId}")
    public ResponseEntity<Void> updateAutomaton(@PathVariable String sessionId,
                                                @RequestBody Automaton automaton) {
        automatonService.updateAutomaton(sessionId, automaton);
        return ResponseEntity.ok().build();
    }

    /**
     * Supprime un automate.
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteAutomaton(@PathVariable String sessionId) {
        automatonService.deleteAutomaton(sessionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Ajoute un état à un automate.
     */
    @PostMapping("/{sessionId}/state")
    public ResponseEntity<State> addState(@PathVariable String sessionId,
                                          @RequestBody Map<String, Double> coordinates) {
        double x = coordinates.getOrDefault("x", 100.0);
        double y = coordinates.getOrDefault("y", 100.0);
        State state = automatonService.addState(sessionId, x, y);
        return ResponseEntity.status(HttpStatus.CREATED).body(state);
    }

    /**
     * Supprime un état.
     */
    @DeleteMapping("/{sessionId}/state/{stateId}")
    public ResponseEntity<Void> removeState(@PathVariable String sessionId,
                                            @PathVariable String stateId) {
        automatonService.removeState(sessionId, stateId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Met à jour un état.
     */
    @PutMapping("/{sessionId}/state/{stateId}")
    public ResponseEntity<Void> updateState(@PathVariable String sessionId,
                                            @PathVariable String stateId,
                                            @RequestBody Map<String, Object> updates) {
        double x = updates.containsKey("x") ? ((Number) updates.get("x")).doubleValue() : 0;
        double y = updates.containsKey("y") ? ((Number) updates.get("y")).doubleValue() : 0;
        Boolean initial = updates.containsKey("initial") ? (Boolean) updates.get("initial") : null;
        Boolean accepting = updates.containsKey("accepting") ? (Boolean) updates.get("accepting") : null;

        automatonService.updateState(sessionId, stateId, x, y, initial, accepting);
        return ResponseEntity.ok().build();
    }

    /**
     * Ajoute une transition.
     */
    @PostMapping("/{sessionId}/transition")
    public ResponseEntity<Transition> addTransition(@PathVariable String sessionId,
                                                    @RequestBody Map<String, String> transitionData) {
        String fromId = transitionData.get("fromId");
        String toId = transitionData.get("toId");
        String symbol = transitionData.get("symbol");

        Transition transition = automatonService.addTransition(sessionId, fromId, toId, symbol);
        return ResponseEntity.status(HttpStatus.CREATED).body(transition);
    }

    /**
     * Supprime une transition.
     */
    @DeleteMapping("/{sessionId}/transition/{transitionId}")
    public ResponseEntity<Void> removeTransition(@PathVariable String sessionId,
                                                 @PathVariable String transitionId) {
        automatonService.removeTransition(sessionId, transitionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère la table de transitions.
     */
    @GetMapping("/{sessionId}/table")
    public ResponseEntity<Map<State, Map<String, java.util.Set<State>>>> getTransitionTable(
            @PathVariable String sessionId) {
        return automatonService.getAutomaton(sessionId)
                .map(automaton -> ResponseEntity.ok(automaton.getTransitionTable()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère les informations d'analyse de l'automate.
     */
    @GetMapping("/{sessionId}/info")
    public ResponseEntity<AutomatonInfo> getAutomatonInfo(@PathVariable String sessionId) {
        return automatonService.getAutomaton(sessionId)
                .map(automaton -> ResponseEntity.ok(analysisService.analyze(automaton)))
                .orElse(ResponseEntity.notFound().build());
    }
}

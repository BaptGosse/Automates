package fr.baptgosse.automates.ui;

import fr.baptgosse.automates.model.Automaton;
import fr.baptgosse.automates.model.State;
import fr.baptgosse.automates.util.RegexGenerator;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.stream.Collectors;

/**
 * Panneau affichant les informations détaillées de l'automate.
 */
public class AutomatonInfoPanel extends VBox {

    private final Automaton automaton;
    private final Label nameLabel;
    private final Label typeLabel;
    private final Label alphabetLabel;
    private final Label statesCountLabel;
    private final Label transitionsCountLabel;
    private final Label initialStateLabel;
    private final Label acceptingStatesLabel;
    private final Label regexLabel;
    private final Label languageDescLabel;
    private final Label isDeterministicLabel;
    private final Label isCompleteLabel;

    public AutomatonInfoPanel(Automaton automaton) {
        this.automaton = automaton;

        // Titre
        Label title = new Label("Informations de l'Automate");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setPadding(new Insets(10));

        // Grille d'informations
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        // Labels pour les informations
        nameLabel = createValueLabel();
        typeLabel = createValueLabel();
        alphabetLabel = createValueLabel();
        statesCountLabel = createValueLabel();
        transitionsCountLabel = createValueLabel();
        initialStateLabel = createValueLabel();
        acceptingStatesLabel = createValueLabel();
        isDeterministicLabel = createValueLabel();
        isCompleteLabel = createValueLabel();
        regexLabel = createValueLabel();
        languageDescLabel = createValueLabel();

        // Construction de la grille
        int row = 0;

        grid.add(createKeyLabel("Nom :"), 0, row);
        grid.add(nameLabel, 1, row++);

        grid.add(createKeyLabel("Type :"), 0, row);
        grid.add(typeLabel, 1, row++);

        grid.add(new Separator(), 0, row++, 2, 1);

        grid.add(createKeyLabel("Alphabet Σ :"), 0, row);
        grid.add(alphabetLabel, 1, row++);

        grid.add(createKeyLabel("Nombre d'états :"), 0, row);
        grid.add(statesCountLabel, 1, row++);

        grid.add(createKeyLabel("Nombre de transitions :"), 0, row);
        grid.add(transitionsCountLabel, 1, row++);

        grid.add(new Separator(), 0, row++, 2, 1);

        grid.add(createKeyLabel("État initial :"), 0, row);
        grid.add(initialStateLabel, 1, row++);

        grid.add(createKeyLabel("États acceptants :"), 0, row);
        grid.add(acceptingStatesLabel, 1, row++);

        grid.add(new Separator(), 0, row++, 2, 1);

        grid.add(createKeyLabel("Déterministe :"), 0, row);
        grid.add(isDeterministicLabel, 1, row++);

        grid.add(createKeyLabel("Complet :"), 0, row);
        grid.add(isCompleteLabel, 1, row++);

        grid.add(new Separator(), 0, row++, 2, 1);

        grid.add(createKeyLabel("Expression régulière :"), 0, row);
        grid.add(regexLabel, 1, row++);

        grid.add(createKeyLabel("Description :"), 0, row);
        grid.add(languageDescLabel, 1, row++);

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        getChildren().addAll(title, new Separator(), scrollPane);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #cccccc; -fx-border-width: 1;");

        refresh();
    }

    private Label createKeyLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 12));
        return label;
    }

    private Label createValueLabel() {
        Label label = new Label("-");
        label.setFont(Font.font("System", 12));
        label.setWrapText(true);
        label.setMaxWidth(300);
        return label;
    }

    public void refresh() {
        // Nom
        nameLabel.setText(automaton.getName());

        // Type
        String type = isDeterministic() ? "AFD (Automate Fini Déterministe)" : "AFN (Automate Fini Non-Déterministe)";
        typeLabel.setText(type);

        // Alphabet
        if (automaton.getAlphabet().isEmpty()) {
            alphabetLabel.setText("∅ (vide)");
        } else {
            String alphabet = "{" + String.join(", ", automaton.getAlphabet()) + "}";
            alphabetLabel.setText(alphabet);
        }

        // Nombre d'états
        statesCountLabel.setText(String.valueOf(automaton.getStates().size()));

        // Nombre de transitions
        transitionsCountLabel.setText(String.valueOf(automaton.getTransitions().size()));

        // État initial
        String initial = automaton.getInitialState()
                .map(State::getLabel)
                .orElse("Aucun");
        initialStateLabel.setText(initial);

        // États acceptants
        String accepting = automaton.getAcceptingStates().stream()
                .map(State::getLabel)
                .collect(Collectors.joining(", "));
        acceptingStatesLabel.setText(accepting.isEmpty() ? "Aucun" : "{" + accepting + "}");

        // Déterministe
        isDeterministicLabel.setText(isDeterministic() ? "✓ Oui" : "✗ Non");
        isDeterministicLabel.setStyle(isDeterministic() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        // Complet
        boolean complete = isComplete();
        isCompleteLabel.setText(complete ? "✓ Oui" : "✗ Non");
        isCompleteLabel.setStyle(complete ? "-fx-text-fill: green;" : "-fx-text-fill: red;");

        // Expression régulière
        String regex = RegexGenerator.generateRegex(automaton);
        regexLabel.setText(regex);
        regexLabel.setStyle("");

        // Description du langage
        languageDescLabel.setText(generateLanguageDescription());
    }

    /**
     * Vérifie si l'automate est déterministe.
     */
    private boolean isDeterministic() {
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
     */
    private boolean isComplete() {
        if (!isDeterministic() || automaton.getAlphabet().isEmpty()) {
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
    private String generateLanguageDescription() {
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

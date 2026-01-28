package fr.baptgosse.automates.ui;

import fr.baptgosse.automates.model.Automaton;
import fr.baptgosse.automates.model.State;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Affichage de la table de transitions.
 */
public class TransitionTableView extends VBox {

    private final Automaton automaton;
    private final GridPane tableGrid;
    private final Label titleLabel;
    private final Label summaryLabel;

    public TransitionTableView(Automaton automaton) {
        this.automaton = automaton;
        this.titleLabel = new Label("Table de Transitions");
        this.summaryLabel = new Label();
        this.tableGrid = new GridPane();

        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setPadding(new Insets(10));

        summaryLabel.setFont(Font.font("System", 11));
        summaryLabel.setPadding(new Insets(5, 10, 10, 10));
        summaryLabel.setStyle("-fx-text-fill: #666666;");

        tableGrid.setHgap(10);
        tableGrid.setVgap(5);
        tableGrid.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(tableGrid);
        scrollPane.setFitToWidth(true);

        getChildren().addAll(titleLabel, summaryLabel, scrollPane);
        setPrefWidth(400);

        refresh();
    }

    public void refresh() {
        // Mise à jour du résumé
        updateSummary();

        tableGrid.getChildren().clear();

        if (automaton.getStates().isEmpty()) {
            Label emptyLabel = new Label("Aucun état défini");
            emptyLabel.setFont(Font.font("System", 12));
            tableGrid.add(emptyLabel, 0, 0);
            return;
        }

        buildTable();
    }

    private void updateSummary() {
        StringBuilder summary = new StringBuilder();

        // Alphabet
        if (!automaton.getAlphabet().isEmpty()) {
            summary.append("Σ = {")
                   .append(String.join(", ", automaton.getAlphabet()))
                   .append("}");
        }

        // États
        summary.append("  |  ")
               .append(automaton.getStates().size())
               .append(" état");
        if (automaton.getStates().size() > 1) {
            summary.append("s");
        }

        // Transitions
        summary.append(", ")
               .append(automaton.getTransitions().size())
               .append(" transition");
        if (automaton.getTransitions().size() > 1) {
            summary.append("s");
        }

        summaryLabel.setText(summary.toString());
    }

    private void buildTable() {
        // Obtenir les états triés
        List<State> states = new ArrayList<>(automaton.getStates());
        states.sort(Comparator.comparing(State::getLabel));

        // Obtenir l'alphabet trié
        List<String> alphabet = new ArrayList<>(automaton.getAlphabet());
        Collections.sort(alphabet);

        if (alphabet.isEmpty()) {
            Label noTransLabel = new Label("Aucune transition définie");
            tableGrid.add(noTransLabel, 0, 0);
            return;
        }

        // En-tête : symboles
        Label emptyCell = new Label("");
        emptyCell.setFont(Font.font("System", FontWeight.BOLD, 12));
        tableGrid.add(emptyCell, 0, 0);

        for (int i = 0; i < alphabet.size(); i++) {
            Label symbolLabel = new Label(alphabet.get(i));
            symbolLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
            symbolLabel.setPadding(new Insets(5));
            tableGrid.add(symbolLabel, i + 1, 0);
        }

        // Obtenir la table de transitions
        Map<State, Map<String, Set<State>>> table = automaton.getTransitionTable();

        // Lignes : états
        int row = 1;
        for (State state : states) {
            // Colonne 0 : nom de l'état avec marqueurs
            String stateLabel = formatStateLabel(state);
            Label stateLabelNode = new Label(stateLabel);
            stateLabelNode.setFont(Font.font("System", FontWeight.BOLD, 12));
            stateLabelNode.setPadding(new Insets(5));
            tableGrid.add(stateLabelNode, 0, row);

            // Colonnes suivantes : transitions
            Map<String, Set<State>> transitions = table.get(state);
            for (int col = 0; col < alphabet.size(); col++) {
                String symbol = alphabet.get(col);
                Set<State> targetStates = transitions.getOrDefault(symbol, Collections.emptySet());

                String targetText;
                if (targetStates.isEmpty()) {
                    targetText = "-";
                } else if (targetStates.size() == 1) {
                    // Pour un AFD, afficher juste le label sans accolades
                    targetText = targetStates.iterator().next().getLabel();
                } else {
                    // Pour un AFN, afficher avec accolades
                    targetText = targetStates.stream()
                            .map(State::getLabel)
                            .collect(Collectors.joining(", ", "{", "}"));
                }

                Label targetLabel = new Label(targetText);
                targetLabel.setPadding(new Insets(5));
                tableGrid.add(targetLabel, col + 1, row);
            }

            row++;
        }
    }

    private String formatStateLabel(State state) {
        StringBuilder sb = new StringBuilder();

        if (state.isInitial()) {
            sb.append("→ ");
        }

        sb.append(state.getLabel());

        if (state.isAccepting()) {
            sb.append(" *");
        }

        return sb.toString();
    }
}

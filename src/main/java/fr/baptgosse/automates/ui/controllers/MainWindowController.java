package fr.baptgosse.automates.ui.controllers;

import fr.baptgosse.automates.ui.AutomatonCanvas;
import fr.baptgosse.automates.ui.AutomatonInfoPanel;
import fr.baptgosse.automates.ui.TransitionTableView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Contrôleur pour la fenêtre principale.
 */
public class MainWindowController {

    @FXML private MenuBar menuBar;
    @FXML private ToolBar toolBar;
    @FXML private ToggleButton selectBtn;
    @FXML private ToggleGroup toolGroup;
    @FXML private ToggleButton addStateBtn;
    @FXML private ToggleButton addTransitionBtn;
    @FXML private SplitPane mainSplitPane;
    @FXML private AnchorPane canvasContainer;
    @FXML private TabPane rightTabPane;
    @FXML private VBox tableContainer;
    @FXML private VBox infoContainer;
    @FXML private Label statusLabel;
    @FXML private Label statusInfoLabel;

    private AutomatonCanvas canvas;
    private TransitionTableView tableView;
    private AutomatonInfoPanel infoPanel;

    @FXML
    public void initialize() {
        // Initialiser le canvas
        canvas = new AutomatonCanvas();

        // Ajouter le canvas au conteneur et le faire remplir tout l'espace
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);
        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        canvasContainer.getChildren().add(canvas);

        // Initialiser la table de transitions
        tableView = new TransitionTableView(canvas.getAutomaton());
        tableContainer.getChildren().add(tableView);
        VBox.setVgrow(tableView, javafx.scene.layout.Priority.ALWAYS);

        // Initialiser le panneau d'informations
        infoPanel = new AutomatonInfoPanel(canvas.getAutomaton());
        infoContainer.getChildren().add(infoPanel);
        VBox.setVgrow(infoPanel, javafx.scene.layout.Priority.ALWAYS);

        // Lier le canvas aux vues pour les mises à jour
        canvas.setOnAutomatonChanged(() -> {
            tableView.refresh();
            infoPanel.refresh();
            updateStatus();
        });

        // Initialiser le statut
        updateStatus();
    }

    // === Actions Menu Fichier ===

    @FXML
    private void handleNew() {
        canvas.clear();
        statusLabel.setText("Nouvel automate créé");
    }

    @FXML
    private void handleOpen() {
        // TODO: Implémenter l'ouverture de fichier
        statusLabel.setText("Ouverture de fichier (à implémenter)");
    }

    @FXML
    private void handleSave() {
        // TODO: Implémenter la sauvegarde
        statusLabel.setText("Sauvegarde (à implémenter)");
    }

    @FXML
    private void handleExportPNG() {
        // TODO: Implémenter l'export PNG
        statusLabel.setText("Export PNG (à implémenter)");
    }

    @FXML
    private void handleExportSVG() {
        // TODO: Implémenter l'export SVG
        statusLabel.setText("Export SVG (à implémenter)");
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    // === Actions Menu Édition ===

    @FXML
    private void handleClear() {
        canvas.clear();
        statusLabel.setText("Automate effacé");
    }

    @FXML
    private void handleUndo() {
        // TODO: Implémenter undo
        statusLabel.setText("Annuler (à implémenter)");
    }

    @FXML
    private void handleRedo() {
        // TODO: Implémenter redo
        statusLabel.setText("Rétablir (à implémenter)");
    }

    // === Actions Menu Automate ===

    @FXML
    private void handleDeterminize() {
        // TODO: Implémenter la déterminisation
        statusLabel.setText("Déterminisation (à implémenter)");
    }

    @FXML
    private void handleMinimize() {
        // TODO: Implémenter la minimisation
        statusLabel.setText("Minimisation (à implémenter)");
    }

    @FXML
    private void handleComplete() {
        // TODO: Implémenter la complétion
        statusLabel.setText("Complétion (à implémenter)");
    }

    @FXML
    private void handleTestWord() {
        // TODO: Implémenter le test de mot
        statusLabel.setText("Test de mot (à implémenter)");
    }

    // === Actions Menu Aide ===

    @FXML
    private void handleDocumentation() {
        // TODO: Ouvrir la documentation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Documentation");
        alert.setHeaderText("Éditeur d'Automates");
        alert.setContentText("Consultez le fichier README.md pour la documentation complète.");
        alert.showAndWait();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("Éditeur d'Automates");
        alert.setContentText("Version 1.0\n\n" +
                "Application JavaFX pour créer, visualiser et manipuler des automates finis.\n\n" +
                "Développé dans le cadre du cours R4-A-12");
        alert.showAndWait();
    }

    // === Actions Toolbar ===

    @FXML
    private void handleSelectTool() {
        canvas.setTool(AutomatonCanvas.Tool.SELECT);
        statusLabel.setText("Outil Sélection activé");
    }

    @FXML
    private void handleAddStateTool() {
        canvas.setTool(AutomatonCanvas.Tool.ADD_STATE);
        statusLabel.setText("Outil Ajout d'État activé - Cliquez sur le canvas");
    }

    @FXML
    private void handleAddTransitionTool() {
        canvas.setTool(AutomatonCanvas.Tool.ADD_TRANSITION);
        statusLabel.setText("Outil Ajout de Transition activé - Sélectionnez deux états");
    }

    @FXML
    private void handleDelete() {
        canvas.deleteSelected();
        statusLabel.setText("Élément supprimé");
    }

    @FXML
    private void handleSetInitial() {
        canvas.toggleInitialOnSelected();
        statusLabel.setText("État initial défini");
    }

    @FXML
    private void handleSetAccepting() {
        canvas.toggleAcceptingOnSelected();
        statusLabel.setText("État acceptant modifié");
    }

    // === Méthodes utilitaires ===

    private void updateStatus() {
        int stateCount = canvas.getAutomaton().getStates().size();
        int transitionCount = canvas.getAutomaton().getTransitions().size();

        statusInfoLabel.setText(String.format("%d état%s, %d transition%s",
                stateCount,
                stateCount > 1 ? "s" : "",
                transitionCount,
                transitionCount > 1 ? "s" : ""));
    }
}

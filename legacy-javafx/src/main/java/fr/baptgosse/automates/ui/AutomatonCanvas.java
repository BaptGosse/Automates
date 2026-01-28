package fr.baptgosse.automates.ui;

import fr.baptgosse.automates.model.Automaton;
import fr.baptgosse.automates.model.State;
import fr.baptgosse.automates.model.Transition;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Canvas pour dessiner et interagir avec l'automate.
 */
public class AutomatonCanvas extends Pane {

    public enum Tool {
        SELECT,
        ADD_STATE,
        ADD_TRANSITION
    }

    private final Canvas canvas;
    private final Automaton automaton;
    private Tool currentTool = Tool.SELECT;
    private State selectedState = null;
    private Transition selectedTransition = null;
    private State transitionStartState = null;
    private Runnable onAutomatonChanged;

    public AutomatonCanvas() {
        this.automaton = new Automaton("Mon Automate");
        this.canvas = new Canvas(800, 600);

        getChildren().add(canvas);

        // Redimensionner le canvas avec le pane
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        // Redessiner quand la taille change
        canvas.widthProperty().addListener((obs, oldVal, newVal) -> draw());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> draw());

        setupMouseHandlers();
        draw();
    }

    private void setupMouseHandlers() {
        canvas.setOnMouseClicked(this::handleMouseClick);
        canvas.setOnMouseMoved(this::handleMouseMove);
    }

    private void handleMouseClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        switch (currentTool) {
            case ADD_STATE:
                addState(x, y);
                break;

            case ADD_TRANSITION:
                handleTransitionClick(x, y);
                break;

            case SELECT:
                handleSelection(x, y, event.getButton());
                break;
        }
    }

    private void handleMouseMove(MouseEvent event) {
        // Pourrait être utilisé pour afficher un aperçu
    }

    private void addState(double x, double y) {
        String label = automaton.generateStateLabel();
        State state = new State(label, x, y);
        automaton.addState(state);
        draw();
        notifyChange();
    }

    private void handleTransitionClick(double x, double y) {
        Optional<State> clickedState = automaton.getStateAt(x, y);

        if (clickedState.isPresent()) {
            if (transitionStartState == null) {
                // Premier clic : sélectionner l'état de départ
                transitionStartState = clickedState.get();
            } else {
                // Deuxième clic : créer la transition
                State endState = clickedState.get();

                // Demander le symbole
                TextInputDialog dialog = new TextInputDialog("a");
                dialog.setTitle("Symbole de transition");
                dialog.setHeaderText("Entrez le(s) symbole(s) pour la transition");
                dialog.setContentText("Symbole(s) (séparés par des virgules):");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(input -> {
                    // Séparer les symboles par des virgules
                    String[] symbols = input.split(",");
                    for (String symbol : symbols) {
                        String trimmedSymbol = symbol.trim();
                        if (!trimmedSymbol.isEmpty()) {
                            Transition transition = new Transition(transitionStartState, endState, trimmedSymbol);
                            automaton.addTransition(transition);
                        }
                    }
                    draw();
                    notifyChange();
                });

                transitionStartState = null;
            }
        } else {
            // Clic dans le vide : annuler
            transitionStartState = null;
        }

        draw();
    }

    private void handleSelection(double x, double y, MouseButton button) {
        Optional<State> clickedState = automaton.getStateAt(x, y);

        if (clickedState.isPresent()) {
            selectedState = clickedState.get();
            selectedTransition = null;
            draw();
        } else {
            // Vérifier si on a cliqué sur une transition
            selectedState = null;
            selectedTransition = getTransitionAt(x, y);
            draw();
        }
    }

    public void deleteSelected() {
        if (selectedState != null) {
            automaton.removeState(selectedState);
            selectedState = null;
            draw();
            notifyChange();
        } else if (selectedTransition != null) {
            // Supprimer toutes les transitions entre les mêmes états
            State from = selectedTransition.getFrom();
            State to = selectedTransition.getTo();

            List<Transition> toRemove = automaton.getTransitions().stream()
                    .filter(t -> t.getFrom().equals(from) && t.getTo().equals(to))
                    .collect(Collectors.toList());

            toRemove.forEach(automaton::removeTransition);
            selectedTransition = null;
            draw();
            notifyChange();
        }
    }

    public void toggleInitialOnSelected() {
        if (selectedState != null) {
            if (selectedState.isInitial()) {
                // Si déjà initial, on le désactive
                selectedState.setInitial(false);
            } else {
                // Retirer le statut initial des autres états
                automaton.getStates().forEach(s -> s.setInitial(false));
                selectedState.setInitial(true);
            }
            draw();
            notifyChange();
        }
    }

    public void toggleAcceptingOnSelected() {
        if (selectedState != null) {
            selectedState.setAccepting(!selectedState.isAccepting());
            draw();
            notifyChange();
        }
    }

    public void clear() {
        automaton.getStates().clear();
        automaton.getTransitions().clear();
        selectedState = null;
        selectedTransition = null;
        transitionStartState = null;
        draw();
        notifyChange();
    }

    /**
     * Trouve une transition proche d'une position donnée.
     */
    private Transition getTransitionAt(double x, double y) {
        double threshold = 15; // Distance seuil pour la sélection

        // Regrouper les transitions par paire (from, to)
        Map<String, List<Transition>> groupedTransitions = new HashMap<>();
        for (Transition transition : automaton.getTransitions()) {
            String key = transition.getFrom().getId() + "->" + transition.getTo().getId();
            groupedTransitions.computeIfAbsent(key, k -> new ArrayList<>()).add(transition);
        }

        for (List<Transition> group : groupedTransitions.values()) {
            if (group.isEmpty()) continue;

            Transition firstTransition = group.get(0);
            State from = firstTransition.getFrom();
            State to = firstTransition.getTo();

            // Ignorer les boucles pour l'instant (plus complexe à détecter)
            if (from.equals(to)) {
                continue;
            }

            // Calculer le point milieu de la transition
            double midX = (from.getX() + to.getX()) / 2;
            double midY = (from.getY() + to.getY()) / 2;

            // Vérifier si le clic est proche du milieu
            double distance = Math.sqrt(Math.pow(x - midX, 2) + Math.pow(y - midY, 2));
            if (distance < threshold) {
                // Retourner la première transition du groupe
                return firstTransition;
            }
        }

        return null;
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Effacer
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Dessiner les transitions d'abord (sous les états)
        drawTransitions(gc);

        // Dessiner les états
        drawStates(gc);

        // Dessiner l'état de départ de transition en cours
        if (transitionStartState != null) {
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(3);
            gc.strokeOval(
                    transitionStartState.getX() - State.getRadius(),
                    transitionStartState.getY() - State.getRadius(),
                    State.getRadius() * 2,
                    State.getRadius() * 2
            );
        }
    }

    private void drawStates(GraphicsContext gc) {
        for (State state : automaton.getStates()) {
            double x = state.getX();
            double y = state.getY();
            double radius = State.getRadius();

            // Cercle de l'état (fond blanc ou bleu clair si sélectionné)
            gc.setFill(state == selectedState ? Color.rgb(220, 240, 255) : Color.WHITE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.5);
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
            gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

            // Double cercle si acceptant (style des exercices)
            if (state.isAccepting()) {
                double innerRadius = radius - 6;
                gc.setLineWidth(1.5);
                gc.strokeOval(x - innerRadius, y - innerRadius, innerRadius * 2, innerRadius * 2);
            }

            // Flèche entrante si initial (longue flèche venant de l'extérieur)
            if (state.isInitial()) {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1.5);

                // Point de départ de la flèche (loin à gauche)
                double arrowStartX = x - radius - 50;
                double arrowEndX = x - radius - 2;

                // Ligne de la flèche
                gc.strokeLine(arrowStartX, y, arrowEndX, y);

                // Pointe de flèche (triangle)
                gc.strokeLine(arrowEndX, y, arrowEndX - 8, y - 4);
                gc.strokeLine(arrowEndX, y, arrowEndX - 8, y + 4);
            }

            // Label de l'état (centré)
            gc.setFill(Color.BLACK);
            gc.setFont(Font.font("Arial", 14));

            // Mesurer la largeur du texte pour le centrer
            String label = state.getLabel();
            Text text = new Text(label);
            text.setFont(gc.getFont());
            double textWidth = text.getLayoutBounds().getWidth();
            double textHeight = text.getLayoutBounds().getHeight();

            gc.fillText(label, x - textWidth / 2, y + textHeight / 4);
        }
    }

    private void drawTransitions(GraphicsContext gc) {
        // Regrouper les transitions par paire (from, to)
        Map<String, List<Transition>> groupedTransitions = new HashMap<>();

        for (Transition transition : automaton.getTransitions()) {
            String key = transition.getFrom().getId() + "->" + transition.getTo().getId();
            groupedTransitions.computeIfAbsent(key, k -> new ArrayList<>()).add(transition);
        }

        // Dessiner chaque groupe de transitions
        for (List<Transition> group : groupedTransitions.values()) {
            if (group.isEmpty()) continue;

            Transition firstTransition = group.get(0);
            State from = firstTransition.getFrom();
            State to = firstTransition.getTo();

            // Regrouper les symboles
            String combinedSymbol = group.stream()
                    .map(Transition::getSymbol)
                    .collect(Collectors.joining(","));

            // Mettre en surbrillance si une des transitions est sélectionnée
            boolean isSelected = group.contains(selectedTransition);
            if (isSelected) {
                gc.setStroke(Color.BLUE);
                gc.setLineWidth(2.5);
            } else {
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1.5);
            }

            // Vérifier si c'est une boucle
            if (from.equals(to)) {
                drawSelfLoop(gc, from, combinedSymbol);
            } else {
                drawArrow(gc, from, to, combinedSymbol);
            }
        }
    }

    private void drawArrow(GraphicsContext gc, State from, State to, String symbol) {
        double x1 = from.getX();
        double y1 = from.getY();
        double x2 = to.getX();
        double y2 = to.getY();

        // Calculer l'angle
        double angle = Math.atan2(y2 - y1, x2 - x1);

        // Points ajustés pour ne pas entrer dans les cercles
        double radius = State.getRadius();
        double startX = x1 + radius * Math.cos(angle);
        double startY = y1 + radius * Math.sin(angle);
        double endX = x2 - radius * Math.cos(angle);
        double endY = y2 - radius * Math.sin(angle);

        // Vérifier s'il y a déjà une transition dans l'autre sens
        boolean hasReverse = automaton.getTransitions().stream()
                .anyMatch(t -> t.getFrom().equals(to) && t.getTo().equals(from));

        if (hasReverse) {
            // Dessiner une courbe si transition dans les 2 sens
            drawCurvedArrow(gc, startX, startY, endX, endY, angle, symbol, 20);
        } else {
            // Ligne droite simple
            gc.setLineWidth(1.5);
            gc.strokeLine(startX, startY, endX, endY);

            // Pointe de flèche
            drawArrowHead(gc, endX, endY, angle);

            // Label au milieu
            double midX = (startX + endX) / 2;
            double midY = (startY + endY) / 2;
            drawTransitionLabel(gc, symbol, midX, midY, angle);
        }
    }

    private void drawCurvedArrow(GraphicsContext gc, double startX, double startY,
                                  double endX, double endY, double angle, String symbol, double curve) {
        // Point de contrôle pour la courbe de Bézier
        double midX = (startX + endX) / 2;
        double midY = (startY + endY) / 2;

        // Décalage perpendiculaire pour la courbure
        double perpAngle = angle + Math.PI / 2;
        double ctrlX = midX + curve * Math.cos(perpAngle);
        double ctrlY = midY + curve * Math.sin(perpAngle);

        // Dessiner la courbe quadratique
        gc.beginPath();
        gc.moveTo(startX, startY);
        gc.quadraticCurveTo(ctrlX, ctrlY, endX, endY);
        gc.stroke();

        // Calculer l'angle à la fin pour la pointe de flèche
        double dx = endX - ctrlX;
        double dy = endY - ctrlY;
        double endAngle = Math.atan2(dy, dx);

        drawArrowHead(gc, endX, endY, endAngle);

        // Label au point de contrôle
        drawTransitionLabel(gc, symbol, ctrlX, ctrlY, angle);
    }

    private void drawArrowHead(GraphicsContext gc, double x, double y, double angle) {
        double arrowLength = 12;
        double arrowAngle = Math.PI / 7;

        double x1 = x - arrowLength * Math.cos(angle - arrowAngle);
        double y1 = y - arrowLength * Math.sin(angle - arrowAngle);
        double x2 = x - arrowLength * Math.cos(angle + arrowAngle);
        double y2 = y - arrowLength * Math.sin(angle + arrowAngle);

        gc.strokeLine(x, y, x1, y1);
        gc.strokeLine(x, y, x2, y2);
    }

    private void drawTransitionLabel(GraphicsContext gc, String symbol, double x, double y, double angle) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 13));

        // Décalage pour positionner le label
        double offsetX = 8 * Math.cos(angle + Math.PI / 2);
        double offsetY = 8 * Math.sin(angle + Math.PI / 2);

        gc.fillText(symbol, x + offsetX, y + offsetY);
    }

    private void drawSelfLoop(GraphicsContext gc, State state, String symbol) {
        double x = state.getX();
        double y = state.getY();
        double radius = State.getRadius();

        // Dessiner une boucle au-dessus de l'état (style des exercices)
        double loopRadius = radius * 0.7;
        double loopCenterY = y - radius - loopRadius;

        gc.setLineWidth(1.5);

        // Arc pour la boucle (angle de départ, étendue de l'arc)
        // Décalage de 45° pour commencer et finir au bon endroit
        double startAngle = 75; // 30° + 45°
        double arcExtent = 300;
        gc.strokeArc(x - loopRadius, loopCenterY - loopRadius,
                     loopRadius * 2, loopRadius * 2,
                     startAngle, arcExtent, javafx.scene.shape.ArcType.OPEN);

        // Pointe de flèche à la fin de la boucle
        double endAngleDegrees = startAngle + arcExtent; // 375° = 15°
        double endAngleRadians = Math.toRadians(endAngleDegrees);

        // Position de la fin de l'arc
        double arrowX = x + loopRadius * Math.cos(endAngleRadians);
        double arrowY = loopCenterY + loopRadius * Math.sin(endAngleRadians);

        // La tangente à l'arc est perpendiculaire au rayon
        // Pour un arc dans le sens horaire, on ajoute 90° à l'angle du rayon
        double tangentAngle = endAngleRadians + Math.PI / 2;
        drawArrowHead(gc, arrowX, arrowY, tangentAngle);

        // Label au-dessus de la boucle
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 13));
        gc.fillText(symbol, x - 8, loopCenterY - loopRadius - 8);
    }

    // Getters et setters
    public Automaton getAutomaton() {
        return automaton;
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
        this.transitionStartState = null;
        draw();
    }

    public void setOnAutomatonChanged(Runnable callback) {
        this.onAutomatonChanged = callback;
    }

    private void notifyChange() {
        if (onAutomatonChanged != null) {
            onAutomatonChanged.run();
        }
    }
}

package fr.baptgosse.automates.model;

import lombok.Data;
import java.util.UUID;

/**
 * Représente un état dans un automate fini.
 */
@Data
public class State {
    private final String id;
    private String label;
    private double x; // Position X pour l'affichage
    private double y; // Position Y pour l'affichage
    private boolean initial;
    private boolean accepting;
    private static final double RADIUS = 30.0; // Rayon pour le dessin

    public State(String label, double x, double y) {
        this.id = UUID.randomUUID().toString();
        this.label = label;
        this.x = x;
        this.y = y;
        this.initial = false;
        this.accepting = false;
    }

    public State(String label, double x, double y, boolean initial, boolean accepting) {
        this.id = UUID.randomUUID().toString();
        this.label = label;
        this.x = x;
        this.y = y;
        this.initial = initial;
        this.accepting = accepting;
    }

    /**
     * Vérifie si un point (x, y) est dans ce cercle d'état.
     */
    public boolean contains(double px, double py) {
        double dx = px - x;
        double dy = py - y;
        return Math.sqrt(dx * dx + dy * dy) <= RADIUS;
    }

    public static double getRadius() {
        return RADIUS;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return id.equals(state.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

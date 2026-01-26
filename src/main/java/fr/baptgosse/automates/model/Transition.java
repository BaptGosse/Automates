package fr.baptgosse.automates.model;

import lombok.Data;

import java.util.UUID;

/**
 * Représente une transition dans un automate fini.
 */
@Data
public class Transition {
    private final String id;
    private final State from;
    private final State to;
    private String symbol;

    public Transition(State from, State to, String symbol) {
        this.id = UUID.randomUUID().toString();
        this.from = from;
        this.to = to;
        this.symbol = symbol != null ? symbol : "";
    }

    /**
     * Vérifie si c'est une ε-transition.
     */
    public boolean isEpsilon() {
        return symbol.isEmpty() || symbol.equals("ε") || symbol.equals("epsilon");
    }

    @Override
    public String toString() {
        return from.getLabel() + " --(" + symbol + ")--> " + to.getLabel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transition that = (Transition) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

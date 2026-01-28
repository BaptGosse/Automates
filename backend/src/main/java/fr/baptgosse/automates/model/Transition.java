package fr.baptgosse.automates.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * Représente une transition dans un automate fini.
 */
public class Transition {
    @JsonProperty("id")
    private final String id;

    @JsonProperty("from")
    private final State from;

    @JsonProperty("to")
    private final State to;

    @JsonProperty("symbol")
    private String symbol;

    public Transition(State from, State to, String symbol) {
        this.id = UUID.randomUUID().toString();
        this.from = from;
        this.to = to;
        this.symbol = symbol != null ? symbol : "";
    }

    // Getters
    public String getId() { return id; }
    public State getFrom() { return from; }
    public State getTo() { return to; }
    public String getSymbol() { return symbol; }

    // Setter
    public void setSymbol(String symbol) { this.symbol = symbol; }

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

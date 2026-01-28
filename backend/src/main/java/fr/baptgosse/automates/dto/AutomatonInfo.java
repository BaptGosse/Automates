package fr.baptgosse.automates.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

/**
 * DTO contenant les informations d'analyse d'un automate.
 */
public class AutomatonInfo {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type; // "AFD" ou "AFN"

    @JsonProperty("isDeterministic")
    private boolean isDeterministic;

    @JsonProperty("isComplete")
    private boolean isComplete;

    @JsonProperty("statesCount")
    private int statesCount;

    @JsonProperty("transitionsCount")
    private int transitionsCount;

    @JsonProperty("alphabet")
    private Set<String> alphabet;

    @JsonProperty("initialState")
    private String initialState;

    @JsonProperty("acceptingStates")
    private Set<String> acceptingStates;

    @JsonProperty("regex")
    private String regex;

    @JsonProperty("languageDescription")
    private String languageDescription;

    // Constructeur sans arguments
    public AutomatonInfo() {}

    // Constructeur avec tous les arguments
    public AutomatonInfo(String name, String type, boolean isDeterministic, boolean isComplete,
                        int statesCount, int transitionsCount, Set<String> alphabet,
                        String initialState, Set<String> acceptingStates, String regex,
                        String languageDescription) {
        this.name = name;
        this.type = type;
        this.isDeterministic = isDeterministic;
        this.isComplete = isComplete;
        this.statesCount = statesCount;
        this.transitionsCount = transitionsCount;
        this.alphabet = alphabet;
        this.initialState = initialState;
        this.acceptingStates = acceptingStates;
        this.regex = regex;
        this.languageDescription = languageDescription;
    }

    // Getters
    public String getName() { return name; }
    public String getType() { return type; }
    public boolean isDeterministic() { return isDeterministic; }
    public boolean isComplete() { return isComplete; }
    public int getStatesCount() { return statesCount; }
    public int getTransitionsCount() { return transitionsCount; }
    public Set<String> getAlphabet() { return alphabet; }
    public String getInitialState() { return initialState; }
    public Set<String> getAcceptingStates() { return acceptingStates; }
    public String getRegex() { return regex; }
    public String getLanguageDescription() { return languageDescription; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setDeterministic(boolean deterministic) { isDeterministic = deterministic; }
    public void setComplete(boolean complete) { isComplete = complete; }
    public void setStatesCount(int statesCount) { this.statesCount = statesCount; }
    public void setTransitionsCount(int transitionsCount) { this.transitionsCount = transitionsCount; }
    public void setAlphabet(Set<String> alphabet) { this.alphabet = alphabet; }
    public void setInitialState(String initialState) { this.initialState = initialState; }
    public void setAcceptingStates(Set<String> acceptingStates) { this.acceptingStates = acceptingStates; }
    public void setRegex(String regex) { this.regex = regex; }
    public void setLanguageDescription(String languageDescription) { this.languageDescription = languageDescription; }
}

package interpreter.grammar.lalr.state;

import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.lalr.LALRGrammar;

import java.io.Serializable;

public class LALRParseManager implements Serializable {
    private static LALRParseManager instance = null;
    public static String grammarFilePath = "src/main/res/grammar.yaml";

    private LALRGrammar grammar;
    private LALRStateMachine stateMachine = null;

    public static LALRParseManager getInstance() {
        if (instance == null) {
            return new LALRParseManager();
        }

        return instance;
    }

    private LALRParseManager() {
        this.grammar = new LALRGrammar(grammarFilePath);
    }

    public LALRGrammar getGrammar() {
        return this.grammar;
    }

    public void runStateMachine() {
        stateMachine = new LALRStateMachine();
    }

    public LALRStateMachine getStateMachine() {
        if (stateMachine == null) {
            runStateMachine();
        }
        return stateMachine;
    }

    public Integer getAction(Integer stateId, GrammarSymbol symbol) {
        if (stateMachine == null) {
            runStateMachine();
        }
        return stateMachine.getAction(stateId, symbol);
    }

    public LALRState getState(Integer stateId) {
        return stateMachine.getState(stateId);
    }
}

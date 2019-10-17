package interpreter.utils.lalr.state;

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;
import interpreter.utils.lalr.GrammarSymbol;
import interpreter.utils.lalr.LALRGrammar;

public class LALRParseManager {
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
        return stateMachine.getAction(stateId, symbol);
    }
}

package interpreter.grammar.lalr.state;

import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.lalr.LALRGrammar;

import java.io.Serializable;

public class LALRParseManager implements Serializable {

    private LALRGrammar grammar;
    private LALRStateMachine stateMachine = null;

    /**
     * Constructor
     */
    public LALRParseManager() {
        this.grammar = LALRGrammar.getGrammar();
        runStateMachine();
    }

    /**
     * build the state machine
     */
    public void runStateMachine() {
        stateMachine = new LALRStateMachine();
    }

    /**
     * get the built state machine
     * @return the state machine
     */
    public LALRStateMachine getStateMachine() {
        if (stateMachine == null) {
            runStateMachine();
        }
        return stateMachine;
    }

    /**
     * get the action from transition table
     * @param stateId the row: id of state
     * @param symbol the column: a terminal of non-terminal symbl
     * @return a integer that represents the action with proper encoding
     */
    public Integer getAction(Integer stateId, GrammarSymbol symbol) {
        if (stateMachine == null) {
            runStateMachine();
        }
        return stateMachine.getAction(stateId, symbol);
    }

    /**
     * get the state with specified id
     * @param stateId a integer, the id of state
     * @return the LALRState for the id
     */
    public LALRState getState(Integer stateId) {
        if (stateMachine == null) {
            runStateMachine();
        }
        return stateMachine.getState(stateId);
    }
}

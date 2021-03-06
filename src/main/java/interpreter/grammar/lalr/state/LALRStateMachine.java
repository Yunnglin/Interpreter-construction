package interpreter.grammar.lalr.state;

import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.lalr.LALRGrammar;
import interpreter.grammar.NonterminalSymbol;
import interpreter.grammar.TerminalSymbol;

import static interpreter.grammar.lalr.LALRGrammar.NIL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LALRStateMachine implements Serializable {
    private ArrayList<LALRState> states;
    private ArrayList<HashMap<GrammarSymbol, Integer>> transitionTable;

    /**
     * Constructor
     */
    public LALRStateMachine() {
        states = new ArrayList<LALRState>();
        transitionTable = new ArrayList<HashMap<GrammarSymbol, Integer>>();

        // add the initial state into the machine
        LALRGrammar grammar = LALRGrammar.getGrammar();
        ArrayList<Integer> posAndLen = grammar.getStartPosAndLen(grammar.getStartSymbol().getSelfText());
        HashSet<LRItem> initialItems = new HashSet<>();
        // create the first LR(1) item with the first production of start symbol
        LRItem startItem = new LRItem(posAndLen.get(0));
        // add the end symbol to the lookAheadSet of the first LR(1) item
        startItem.addToLookAheadSet(grammar.getEndSymbol());
        initialItems.add(startItem);
        states.add(new LALRState(initialItems));

        // 构建初始 LALR(1) 状态机
        build();
    }

    /**
     * build the states net and transition table
     */
    private void build() {
        // two flags that makes the list of states a queue
        int head = 0;
        int tail = states.size();
        LALRGrammar grammar = LALRGrammar.getGrammar();
        NonterminalSymbol startSymbol = grammar.getStartSymbol();
        TerminalSymbol endSymbol = grammar.getEndSymbol();
        ArrayList<Integer> posAndLen = grammar.getStartPosAndLen(startSymbol.getSelfText());
        Integer acceptProductionId = posAndLen.get(0);

        // extend new states
        while(head != tail) {
            LALRState curState = states.get(head);
            // add a new row to the parsing table
            HashMap<GrammarSymbol, Integer> newRow = new HashMap<>();
            transitionTable.add(newRow);
            Set<GrammarSymbol> symbols = curState.getPartition().keySet();
            for (GrammarSymbol symbol : symbols) {
                if (symbol == NIL) {
                    // has NIL as its partition key, that is,
                    // some items of this state can do reducing
                    HashMap<TerminalSymbol, Integer> reduceMap = curState.getReduceMap();
                    for (TerminalSymbol terminalSymbol: reduceMap.keySet()) {
                        int reduceProductionId = reduceMap.get(terminalSymbol);
                        int newAction = -(reduceProductionId+1);
                        if (terminalSymbol.equals(endSymbol) && reduceProductionId == acceptProductionId) {
                            // the reduction produces the start symbol
                            // the action should be accept
                            newAction = 0;
                        }

                        // reduce: -(productionId + 1)
                        newRow.put(terminalSymbol, newAction);
                    }
                } else if (grammar.isTerminalSymbol(symbol.getSelfText())) {
                    // the symbol is a terminal symbol
                    // should do shifting and may create a new state
                    LALRState nextState = curState.createNextState(symbol);
                    if (nextState == null) {
                        System.out.println("an error occurred: a symbol in the partition but no state returned");
                    }
                    // id of the destination state
                    int targetId = addStateIfNotExisted(nextState);
                    // represents shift
                    int newAction = targetId + 1;

                    if (newRow.containsKey(symbol) && newRow.get(symbol) != newAction) {
                        // has a shift conflict
                        reportConflict(head, (TerminalSymbol) symbol, newAction);
                    }

                    // shift: targetStateId + 1
                    newRow.put(symbol, newAction);
                } else {
                    // the symbol is a non-terminal symbol
                    // should fill the goto part
                    LALRState nextState = curState.createNextState(symbol);
                    if (nextState == null) {
                        System.out.println("an error occurred: a symbol in the partition but no state returned");
                    }
                    // id of destination state
                    int targetId = addStateIfNotExisted(nextState);
                    // represents goto (the same as shift, distinguish two table with symbol key)
                    int newAction = targetId + 1;

                    newRow.put(symbol, newAction);
                }
            }

            head++;
            tail = states.size();
        }
    }

    /**
     * add a state to the list of states if the list does not contain the state
     * @param state the state to add
     * @return the id of the state in the list
     */
    private int addStateIfNotExisted(LALRState state) {
        int len = states.size();
        for (int i=0; i<len; ++i) {
            if (state.equals(states.get(i))) {
                return i;
            }
        }
        states.add(state);
        return len;
    }

    /**
     * when add a transition into the table, check if there has been a value at the position.
     * if there is, that shows there is a conflict, then report the conflict in the console
     * @param index the row: the id of state
     * @param symbol the column: the symbol
     * @param newActionCode the new action that will add to the position
     */
    private void reportConflict(int index, TerminalSymbol symbol, int newActionCode) {
        HashMap<GrammarSymbol, Integer> row = transitionTable.get(index);
        Integer oldActionCode = row.get(symbol);
        if (oldActionCode == null) {
            return;
        }
        String oldAction = oldActionCode > 0 ? "shift" :
                            oldActionCode < 0 ? "reduce" : "accept";
        String newAction = newActionCode > 0 ? "shift" :
                            newActionCode < 0 ? "reduce" : "accept";
        oldActionCode = oldActionCode != 0 ? Math.abs(oldActionCode) - 1 : oldActionCode;
        newActionCode = newActionCode != 0 ? Math.abs(newActionCode) - 1 : newActionCode;
        System.out.println(oldAction + " " + newAction + " conflict:");
        System.out.println("\tState " + index + ": " + symbol.getSelfText() +
                " -> " + oldAction + " " + oldActionCode +
                " | " + newAction + " " + newActionCode);
    }

    /**
     * get all states of the state machine
     * @return the list of state
     */
    public ArrayList<LALRState> getStates() {
        return states;
    }

    /**
     * get a state with specified id
     * @param stateId the id of the state
     * @return a LALRState
     */
    public LALRState getState(Integer stateId) {
        return this.states.get(stateId);
    }

    /**
     * get the transition table
     * @return a list of hash map that represents the table
     */
    public ArrayList<HashMap<GrammarSymbol, Integer>> getTransitionTable() {
        return transitionTable;
    }

    /**
     * get the action in specified place in the transition table
     * @param stateId the row
     * @param symbol the column
     * @return the encoded integer that represents the action
     */
    public Integer getAction(Integer stateId, GrammarSymbol symbol) {
        return transitionTable.get(stateId).get(symbol);
    }
}

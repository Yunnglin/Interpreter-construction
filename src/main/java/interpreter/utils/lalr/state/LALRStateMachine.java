package interpreter.utils.lalr.state;

import interpreter.utils.lalr.GrammarSymbol;
import interpreter.utils.lalr.LALRGrammar;
import interpreter.utils.lalr.NonterminalSymbol;
import interpreter.utils.lalr.TerminalSymbol;

import static interpreter.utils.lalr.LALRGrammar.NIL;

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
        LALRGrammar grammar = LALRParseManager.getInstance().getGrammar();
        ArrayList<Integer> posAndLen = grammar.getStartPosAndLen(grammar.getStartSymbol().getSelfText());
        HashSet<LRItem> initialItems = new HashSet<>();
        // create the first LR(1) item with the first production of start symbol
        LRItem startItem = new LRItem(posAndLen.get(0));
        // add the end symbol to the lookAheadSet of the first LR(1) item
        startItem.addToLookAheadSet(grammar.getEndSymbol());
        initialItems.add(startItem);
        states.add(new LALRState(initialItems));

        build();
    }

    /**
     * build the states net and transition table
     */
    private void build() {
        // two flags that makes the list of states a queue
        int head = 0;
        int tail = states.size();
        LALRGrammar grammar = LALRParseManager.getInstance().getGrammar();
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

    public ArrayList<LALRState> getStates() {
        return states;
    }

    public LALRState getState(Integer stateId) {
        return this.states.get(stateId);
    }

    public ArrayList<HashMap<GrammarSymbol, Integer>> getTransitionTable() {
        return transitionTable;
    }

    public Integer getAction(Integer stateId, GrammarSymbol symbol) {
        return transitionTable.get(stateId).get(symbol);
    }
}

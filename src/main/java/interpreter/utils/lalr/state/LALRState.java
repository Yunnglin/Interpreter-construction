package interpreter.utils.lalr.state;

import interpreter.utils.lalr.GrammarSymbol;
import interpreter.utils.lalr.LALRGrammar;
import static interpreter.utils.lalr.LALRGrammar.NIL;
import interpreter.utils.lalr.TerminalSymbol;

import java.io.Serializable;
import java.util.*;

public class LALRState implements Serializable {
    private HashSet<LRItem> basicSet;
    private HashSet<LRItem> extendSet;

    private HashMap<GrammarSymbol, ArrayList<LRItem>> partition;
    private HashMap<TerminalSymbol, Integer> reduceMap;

    public LALRState(HashSet<LRItem> basicSet) {
        this.basicSet = basicSet;
        this.extendSet = new HashSet<LRItem>();

        makeClosure();
        makePartition();
        makeReduce();
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }

        LALRState state = (LALRState) obj;

        return basicSet.equals(state.basicSet);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("LALR(1) State: \n");
        stringBuilder.append("\tbasic:\n");
        for (LRItem item : basicSet) {
            stringBuilder.append("\t");
            stringBuilder.append(item.toString());
            stringBuilder.append("\n");
        }
        stringBuilder.append("\textended:\n");
        for (LRItem item : extendSet) {
            stringBuilder.append("\t");
            stringBuilder.append(item.toString());
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    /**
     * get the closure set of LR(1) items
     */
    private void makeClosure() {
        Stack<LRItem> itemStack = new Stack<>();

        LALRGrammar grammar = LALRParseManager.getInstance().getGrammar();

        for (LRItem item : basicSet) {
            itemStack.push(item);
        }

        while(!itemStack.empty()) {
            LRItem item = itemStack.pop();
            GrammarSymbol left = item.getCurSymbol();

            // is a terminal symbol
            if (left == NIL || grammar.isTerminalSymbol(left.getSelfText())) {
                continue;
            }

            // get the new look ahead set
            HashSet<TerminalSymbol> newLookAhead = item.getNewLookAheadInClosure();
            ArrayList<Integer> startPosAndLen = grammar.getStartPosAndLen(left.getSelfText());
            for (int i=startPosAndLen.get(0); i<startPosAndLen.get(0)+startPosAndLen.get(1); ++i) {
                LRItem newitem = new LRItem(i, 0, newLookAhead);
                if (!basicSet.contains(newitem) && !extendSet.contains(newitem)) {
                    extendSet.add(newitem);
                    itemStack.push(newitem);
                    // TODO remove redundant item that has greater look ahead set
                }
            }
        }
    }

    /**
     * create the next state if get a specific symbol
     * @param symbol the next symbol
     * @return
     */
    public LALRState createNextState(GrammarSymbol symbol) {
        ArrayList<LRItem> items = partition.get(symbol);

        if (items == null) {
            return null;
        }

        HashSet<LRItem> newBasicSet = new HashSet<>();
        for (LRItem item : items) {
            newBasicSet.add(item.shiftDot());
        }

        return new LALRState(newBasicSet);
    }

    /**
     * make partitions according to the symbols behind the dot of all items
     */
    private void makePartition() {
        partition = new HashMap<>();

        Iterator<LRItem> basicIt = basicSet.iterator();
        Iterator<LRItem> extendedIt = extendSet.iterator();
        Iterator<LRItem> it = basicIt;

        // first scan basic set
        while(it.hasNext()) {
            LRItem item = it.next();
            GrammarSymbol firstSymbol = item.getCurSymbol();
            if (partition.containsKey(firstSymbol)) {
                partition.get(firstSymbol).add(item);
            } else {
                partition.put(firstSymbol, new ArrayList<LRItem>());
                partition.get(firstSymbol).add(item);
            }

            // next scan extended set
            if (it == basicIt && !it.hasNext()) {
                it = extendedIt;
            }
        }
    }

    private void makeReduce() {
        reduceMap = new HashMap<>();
        if (this.partition.containsKey(NIL)) {
            ArrayList<LRItem> items = this.partition.get(NIL);
            for (LRItem item : items) {
                HashSet<TerminalSymbol> lookAheadSet = item.getLookAheadSet();
                for (TerminalSymbol symbol : lookAheadSet) {
                    if (reduceMap.containsKey(symbol) && reduceMap.get(symbol) != item.getProductionId()) {
                        System.out.println("reduce reduce conflict: " +
                                symbol.getSelfText() + " -> " +
                                reduceMap.get(symbol) + ", " + item.getProductionId());
                        System.out.println(this.toString());
                        continue;
                    }

                    reduceMap.put(symbol, item.getProductionId());
                }
            }
        }
    }

    public HashMap<TerminalSymbol, Integer> getReduceMap() {
        return reduceMap;
    }

    public HashMap<GrammarSymbol, ArrayList<LRItem>> getPartition() {
        return partition;
    }
}

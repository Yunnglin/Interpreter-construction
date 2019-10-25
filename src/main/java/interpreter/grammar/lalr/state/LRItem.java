package interpreter.grammar.lalr.state;

import interpreter.grammar.TokenTag;
import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.Production;
import interpreter.grammar.TerminalSymbol;
import interpreter.grammar.lalr.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static interpreter.grammar.lalr.LALRGrammar.NIL;

public class LRItem implements Serializable {
    private HashSet<TerminalSymbol> lookAheadSet;
    private int productionId;
    private int dotPos;

    /**
     * Constructor, set the dotPos 0 and ookAheadSet a empty set
     * @param productionId
     */
    public LRItem(int productionId) {
        this(productionId, 0);
    }

    /**
     * Constructor, set lookAheadSet a empty set
     * @param productionId the id of the production
     * @param dotPos
     */
    public LRItem(int productionId, int dotPos) {
        this(productionId, dotPos, new HashSet<TerminalSymbol>());
    }

    /**
     * Constructor
     * @param productionId the id of the production
     * @param dotPos the initial position of dot
     * @param lookAheadSet the set of look ahead symbols
     */
    public LRItem(int productionId, int dotPos, HashSet<TerminalSymbol> lookAheadSet) {
        this.lookAheadSet = lookAheadSet;
        this.productionId = productionId;
        LALRGrammar grammar = LALRGrammar.getGrammar();
        Production production = grammar.getProductions().get(productionId);

        if (production.getRightSymbols().size() == 1 && production.getRightSymbols().get(0) == NIL) {
            dotPos = 1;
        }
        else if (dotPos >= production.getRightSymbols().size()) {
            dotPos = production.getRightSymbols().size();
        }

        this.dotPos = dotPos;
    }

    @Override
    protected Object clone() {
        HashSet<TerminalSymbol> newLookAhead = new HashSet<>(lookAheadSet);

        return new LRItem(productionId, dotPos, newLookAhead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lookAheadSet, productionId, dotPos);
    }

    /**
     * check whether two LR(1) items are the same
     * @param obj the other LR(1) item
     * @return if this is the same as the other item
     */
    @Override
    public boolean equals(Object obj) {
        LRItem item = (LRItem) obj;

        if (super.equals(obj)) {
            return true;
        }

        if (dotPos != item.getDotPos()) {
            return false;
        }

        if (productionId != item.getProductionId()) {
            return false;
        }

        if (lookAheadSetEquals(item) != 0) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        LALRGrammar grammar = LALRGrammar.getGrammar();
        Production production = grammar.getProductions().get(productionId);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(production.getLeft().getSelfText());
        stringBuilder.append(" -> ");

        ArrayList<GrammarSymbol> rightSymbols = production.getRightSymbols();
        for (GrammarSymbol symbol : lookAheadSet) {
            for (int i=0; i<rightSymbols.size(); ++i) {
                if (dotPos == i) {
                    stringBuilder.append(". ");
                }
                stringBuilder.append(rightSymbols.get(i).getSelfText());
                stringBuilder.append(" ");
            }
            if (dotPos == rightSymbols.size()) {
                stringBuilder.append(". ");
            }
            stringBuilder.append(", '").append(symbol.getSelfText()).append("'");
            stringBuilder.append("]\n");
        }
        return stringBuilder.toString();
    }

    /**
     * get the set of look ahead symbols
     * @return a set of grammar symbols
     */
    public HashSet<TerminalSymbol> getLookAheadSet() {
        return lookAheadSet;
    }

    /**
     * get the id of the production
     * @return a int value, the id of production
     */
    public int getProductionId() {
        return productionId;
    }

    /**
     * get the current position of the dot
     * @return
     */
    public int getDotPos() {
        return dotPos;
    }

    /**
     * compares the lookAheadSet of two LR(1) item
     * @param item the LR(1) item to compare with
     * @return 0 represents equal，1 represents the lookAheadSet of this contains the other
     *          -1 represents the lookAheadSet of parameter contains the lookAheadSet of this
     *          Integer.MAX_VALUE represents the none of the two set contain the other.
     */
    private int lookAheadSetEquals(LRItem item) {
        boolean containsItem = lookAheadSet.containsAll(item.getLookAheadSet());
        boolean containsSelf = item.getLookAheadSet().containsAll(lookAheadSet);

        // the same set.
        if (containsItem && containsSelf) {
            return 0;
        }

        // this set contains the other.
        if (containsItem) {
            return 1;
        }

        // the set of param contains this set.
        if (containsSelf) {
            return -1;
        }

        // cannot compare the two sets.
        return Integer.MAX_VALUE;
    }

    /**
     * whether this covers the other LR(1) item, that is, the productions are the same
     * and the look ahead set contains the set of the other item
     * @param item the item to compare (whether be contained)
     * @return boolean, true if this covers the other
     */
    public boolean covers(LRItem item) {
        return productionId == item.productionId && dotPos == item.getDotPos()
                && lookAheadSetEquals(item) == 1;
    }

    /**
     * shift the dot forward
     * @return the new LR(1) item after the dot shifted
     */
    public LRItem shiftDot() {
        int newDot = dotPos + 1;
        HashSet<TerminalSymbol> newLookAhead = new HashSet<>(lookAheadSet);

        return new LRItem(productionId, newDot, newLookAhead);
    }

    /**
     * get current symbol behind the dot
     * @return a grammar symbol
     */
    public GrammarSymbol getCurSymbol() {
        LALRGrammar grammar = LALRGrammar.getGrammar();
        Production production = grammar.getProductions().get(productionId);
        if (dotPos > production.getRightSymbols().size()-1) {
            return NIL;
        }

        return production.getRightSymbols().get(dotPos);
    }

    /**
     * get the look ahead set of the closure LR(1) item
     * the set is the first set of (rest string + lookAhead)
     * @return new lookAheadSet, including terminal symbols
     */
    public HashSet<TerminalSymbol> getNewLookAheadInClosure() {
        LALRGrammar grammar = LALRGrammar.getGrammar();
        Production production = grammar.getProductions().get(productionId);
        ArrayList<GrammarSymbol> right = production.getRightSymbols();

        if (dotPos == right.size()) {
            return null;
        }

        if (dotPos == right.size() - 1) {
            return new HashSet<>(lookAheadSet);
        }

        HashSet<TerminalSymbol> firstSet = new HashSet<>();
        boolean nullable = false;

        for (int i = dotPos+1; i< right.size(); i++) {
            HashSet<String> set = grammar.getFirstSet(right.get(i));
            for (String symbol : set) {
                if (symbol.equals(NIL.getSelfText())) {
                    nullable = true;
                } else {
                    firstSet.add(TokenTag.valueOf(symbol));
                }
            }

            if (!nullable) {
                break;
            }
        }

        if (nullable) {
            firstSet.addAll(lookAheadSet);
        }

        return firstSet;
    }

    /**
     * put a set as the look ahead set for this LRItem
     * @param set
     */
    public void setLookAheadSet(HashSet<TerminalSymbol> set) {
        this.lookAheadSet = set;
    }

    /**
     * add a symbol to look ahead set
     * @param symbol the grammar symbol to add
     * @return whether the symbol existed in the set before
     */
    public boolean addToLookAheadSet(TerminalSymbol symbol) {
        return this.lookAheadSet.add(symbol);
    }

    /**
     * add a set of symbol to look ahead set
     * @param symbols the set of grammar symbols to add
     * @return whether the process succeed
     */
    public boolean addToLookAheadSet(Set<TerminalSymbol> symbols) {
        return this.lookAheadSet.addAll(symbols);
    }
}

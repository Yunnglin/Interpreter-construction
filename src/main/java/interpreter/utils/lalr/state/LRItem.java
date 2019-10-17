package interpreter.utils.lalr.state;

import interpreter.Const;
import interpreter.utils.lalr.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static interpreter.utils.lalr.LALRGrammar.NIL;
import static interpreter.utils.lalr.LALRGrammar.Nil;

public class LRItem implements Serializable {
    private HashSet<TerminalSymbol> lookAheadSet;
    private int productionId;
    private int dotPos;

    public LRItem(int productionId) {
        this(productionId, 0);
    }

    public LRItem(int productionId, int dotPos) {
        this(productionId, dotPos, new HashSet<TerminalSymbol>());
    }

    public LRItem(int productionId, int dotPos, HashSet<TerminalSymbol> lookAheadSet) {
        this.lookAheadSet = lookAheadSet;
        this.productionId = productionId;
        LALRGrammar grammar = LALRParseManager.getInstance().getGrammar();
        Production production = grammar.getProductions().get(productionId);

        if (production.getRightSymbols().size() == 1 && production.getRightSymbols().get(0) == Nil.NIL) {
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
        LALRGrammar grammar = LALRParseManager.getInstance().getGrammar();
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

    public HashSet<TerminalSymbol> getLookAheadSet() {
        return lookAheadSet;
    }

    public int getProductionId() {
        return productionId;
    }

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
        LALRGrammar grammar = LALRParseManager.getInstance().getGrammar();
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
        LALRGrammar grammar = LALRParseManager.getInstance().getGrammar();
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
                    firstSet.add(Const.TokenTag.valueOf(symbol));
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

    public void setLookAheadSet(HashSet<TerminalSymbol> set) {
        this.lookAheadSet = set;
    }

    public boolean addToLookAheadSet(TerminalSymbol symbol) {
        return this.lookAheadSet.add(symbol);
    }

    public boolean addToLookAheadSet(Set<TerminalSymbol> symbols) {
        return this.lookAheadSet.addAll(symbols);
    }
}

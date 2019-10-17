package interpreter.utils.lalr;

import interpreter.utils.lalr.GrammarSymbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Production implements Serializable {
    private GrammarSymbol left;
    private ArrayList<GrammarSymbol> rightSymbols;

    public Production(GrammarSymbol left, ArrayList<GrammarSymbol> right) {
        this.left = left;
        this.rightSymbols = right;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, rightSymbols);
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }

        Production production = (Production) obj;

        if (!left.equals(production.left)) {
            return false;
        }

        if (!rightSymbols.equals(production.rightSymbols)) {
            return false;
        }

        return true;
    }

    public GrammarSymbol getLeft() {
        return left;
    }

    public ArrayList<GrammarSymbol> getRightSymbols() {
        return rightSymbols;
    }
}

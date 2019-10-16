package interpreter.utils.lalr;

import interpreter.utils.lalr.GrammarSymbol;

import java.util.ArrayList;

public class Production {
    private GrammarSymbol left;
    private ArrayList<GrammarSymbol> rightSymbols;

    public Production(GrammarSymbol left, ArrayList<GrammarSymbol> right) {
        this.left = left;
        this.rightSymbols = right;
    }

    @Override
    public boolean equals(Object obj) {
        Production production = (Production) obj;
        if (!left.equals(production)) {

        }
        return super.equals(obj);
    }
}

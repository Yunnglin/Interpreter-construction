package interpreter.grammar.lalr;

import interpreter.grammar.GrammarSymbol;

public enum Nil implements GrammarSymbol {
    NIL;

    @Override
    public String getSelfText() {
        return this.toString();
    }
}

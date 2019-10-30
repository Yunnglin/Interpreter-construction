package interpreter.grammar;

import interpreter.grammar.GrammarSymbol;

public interface NonterminalSymbol extends GrammarSymbol {
    public String getSelfText();
}

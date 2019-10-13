package interpreter.utils.lalr;

import java.util.ArrayList;

public class Production {
    private UnterminalSymbol produceSymbol;
    private int symbolNum;
    private ArrayList<GrammarSymbol> symbols;

    public Production(UnterminalSymbol produceSymbol, int symbolNum, GrammarSymbol... symbols) {
        this.produceSymbol = produceSymbol;
        this.symbolNum = symbolNum;
        this.symbols = new ArrayList<GrammarSymbol>(symbolNum);

        for (int i = 0; i<symbolNum && i< symbols.length; ++i) {
            this.symbols.set(i, symbols[i]);
        }
    }

    public UnterminalSymbol getProduceSymbol() {
        return produceSymbol;
    }

    public int getSymbolNum() {
        return symbolNum;
    }

    public ArrayList<GrammarSymbol> getSymbols() {
        return symbols;
    }
}

package interpreter.utils.lalr.state;

import interpreter.utils.lalr.Production;
import interpreter.utils.lalr.TerminalSymbol;

import java.util.HashSet;

public class LRItem {
    private HashSet<TerminalSymbol> lookAheadSet;
    private Production production;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

package interpreter.intermediate;

import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;

import java.util.Stack;

public class Env {

    private Stack<SymTbl> symTblStack;
    private int curNestingLevel;

    public Env() {
        symTblStack = new Stack<SymTbl>();
        curNestingLevel = 0;
    }

    /**
     * push a new symbol table into the stack of symbol tables
     * @param table
     */
    public void pushSymTbl(SymTbl table) {
        symTblStack.push(table);
        curNestingLevel++;
    }

    /**
     * pop the top table out of the stack of symbol tables
     * @return the popped item
     */
    public SymTbl popSymTbl() {
        curNestingLevel--;
        return symTblStack.pop();
    }

    /**
     * find a symbol table entry in available scope
     * @param name the name of the entry
     * @return the entry found or null if the entry is not in all tables
     */
    public SymTblEntry findSymTblEntry(String name) {
        SymTblEntry entry;
        for (int i = curNestingLevel; i >= 0; i--) {
            if((entry = symTblStack.get(i).find(name)) != null) {
                return entry;
            }
        }

        return null;
    }

}

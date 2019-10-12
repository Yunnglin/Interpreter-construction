package interpreter.intermediate.sym;

import java.util.HashMap;

public class SymTblEntry {

    private String name;
    private HashMap<SymTbl.SymTblKey, Object> valuesMap;

    public SymTblEntry(String name) {
        this.name = name;
        this.valuesMap = new HashMap<SymTbl.SymTblKey, Object>();
    }

    public String getName() {
        return name;
    }
}

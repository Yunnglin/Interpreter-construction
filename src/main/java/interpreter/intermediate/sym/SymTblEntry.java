package interpreter.intermediate.sym;

import java.util.HashMap;
import interpreter.intermediate.sym.SymTbl.SymTblKey;

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

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue(SymTblKey key) {
        return this.valuesMap.get(key);
    }

    public Object addValue(SymTblKey key, Object value) {
        return this.valuesMap.put(key, value);
    }
}

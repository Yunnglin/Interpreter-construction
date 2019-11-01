package interpreter.intermediate.sym;

import java.util.HashMap;
import java.util.Set;

import interpreter.intermediate.sym.SymTbl.SymTblKey;

public class SymTblEntry {

    private String name;
    private HashMap<SymTbl.SymTblKey, Object> valuesMap;

    public SymTblEntry copy() {
        SymTblEntry entry = new SymTblEntry(this.name);
        Set<SymTblKey> symTblKeys = this.valuesMap.keySet();

        for (SymTblKey key : symTblKeys) {
            entry.addValue(key, this.valuesMap.get(key));
        }

        return entry;
    }

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

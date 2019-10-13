package interpreter.intermediate.sym;

import java.util.LinkedHashMap;

public class SymTbl {

    public enum SymTblKey {

    }

    private LinkedHashMap<String, SymTblEntry> entries;
    private int nestingLevel;

    /**
     * add an entry to the table
     * @param entry the entry to add
     */
    public void addEntry(SymTblEntry entry) {
        entries.put(entry.getName(), entry);
    }

    /**
     * remove the entry from the table
     * @param entry the entry to remove
     */
    public void removeEntry(SymTblEntry entry) {
        entries.remove(entry.getName());
    }

    /**
     * find an entry with specific name in the table
     * @param name the name of the entry
     * @return the entry or null if it is not in the table
     */
    public SymTblEntry find(String name) {
        return entries.get(name);
    }

}

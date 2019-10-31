package interpreter.intermediate.sym;

import java.util.LinkedHashMap;

public class SymTbl {

    public enum SymTblKey {
        LINE, VALUE, TYPE, ARRAY_SIZE, SYMTBL, FUNC_PROTOTYPE, FUNC_BODY
    }

    private LinkedHashMap<String, SymTblEntry> entries;
    private int nestingLevel;

    /**
     * Constructor
     * @param level nesting level
     */
    public SymTbl(int level) {
        this.nestingLevel = level;
        this.entries = new LinkedHashMap<>();
    }

    /**
     * Constructor
     */
    public SymTbl() {
        this.entries = new LinkedHashMap<>();
    }

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

    public int getNestingLevel() {
        return this.nestingLevel;
    }

    public void setNestingLevel(int level) {
        this.nestingLevel = level;
    }

    public LinkedHashMap<String, SymTblEntry> getEntries() {
        return entries;
    }
}

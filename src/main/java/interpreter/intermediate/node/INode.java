package interpreter.intermediate.node;

        import interpreter.utils.lalr.GrammarSymbol;

        import java.util.ArrayList;
        import java.util.HashMap;

public class INode {
    public enum INodeKey {
        ID, VALUE, LINE, NAME
    }

    private GrammarSymbol symbol;
    private HashMap<INodeKey, Object> values;
    private ArrayList<INode> children;

    public INode(GrammarSymbol symbol) {
        this.symbol = symbol;
        this.values = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public ArrayList<INode> getChildren() {
        return children;
    }

    public GrammarSymbol getSymbol() {
        return symbol;
    }

    public Object getAttribute(INodeKey key) {
        return values.get(key);
    }

    public Object setAttribute(INodeKey key, Object value) {
        return values.put(key, value);
    }

    public void addChild(INode child) {
        this.children.add(child);
    }
}

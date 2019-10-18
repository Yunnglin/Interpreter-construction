package interpreter.intermediate.node;

import interpreter.utils.lalr.GrammarSymbol;

import java.util.ArrayList;
import java.util.HashMap;

public class INode {
    public enum INodeKey {
        ID, VALUE, LINE, NAME
    }

    private int level;
    private GrammarSymbol symbol;
    private HashMap<INodeKey, Object> values;
    private ArrayList<INode> children;

    public INode(GrammarSymbol symbol) {
        this.symbol = symbol;
        this.values = new HashMap<>();
        this.children = new ArrayList<>();
        this.level = 0;
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
        // bottom-up method (recursive way)
        treeLevelInc(child);
    }

    public int getLevel() {
        return level;
    }

    private void treeLevelInc(INode root) {
        root.level = root.level + 1;
        for (INode child : root.getChildren()) {
            treeLevelInc(child);
        }
    }

    public String getAllChild() {
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<INode> nodes = new ArrayList<>();
        nodes.addAll(0, this.getChildren());
        while (true) {
            if (nodes.isEmpty())
                break;
            INode first = nodes.get(0);
            nodes.remove(0);
            int depth = first.getLevel()-1;
            for (int i = 0; i < depth; i++) {
                stringBuilder.append('\t');
            }
            stringBuilder.append(first.getSymbol().getSelfText()).append('\n');
            nodes.addAll(0, first.getChildren());
        }
        return stringBuilder.toString();

    }
}

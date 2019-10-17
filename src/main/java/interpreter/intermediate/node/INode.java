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

    public String getAllChild(){
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<INode> nodes = new ArrayList<>();
        nodes.addAll(0,this.getChildren());
        int depth=1;
        while (true){
            if(nodes.isEmpty())
                break;
            INode first = nodes.get(0);
            nodes.remove(0);
            for (int i = 0; i <depth ; i++) {
                stringBuilder.append('\t');
            }
            stringBuilder.append(first.getSymbol().getSelfText()).append('\n');
            nodes.addAll(0,first.getChildren());
            depth++;
        }
        return stringBuilder.toString();

    }
}

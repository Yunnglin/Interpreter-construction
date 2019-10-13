package interpreter.intermediate.node;

        import interpreter.utils.lalr.GrammarSymbol;

        import java.util.ArrayList;
        import java.util.HashMap;

public class INode {
    public enum INodeKey {
        ID, VALUE, LINE
    }

    private GrammarSymbol symbol;
    private HashMap<INodeKey, Object> values;
    private ArrayList<INode> children;
}

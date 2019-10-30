package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.env.Env;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;

public class ExprStmt extends BaseExecutor {
    public ExprStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        // expression ;
        if (!root.getSymbol().equals(LALRNonterminalSymbol.EXPR_STMT)) {
            throw new Exception("parse error in expression statement at line " +
                    root.getAttribute(INode.INodeKey.LINE));
        }
        return executeNode(root.getChild(0));
    }
}

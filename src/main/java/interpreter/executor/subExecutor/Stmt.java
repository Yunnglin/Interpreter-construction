package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.env.Env;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;

public class Stmt extends BaseExecutor {

    public Stmt(Env env){
        super(env);
    }
    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.STMT)) {
            throw new Exception("parse error in stmt");
        }
        executeNode(root.getChild(0));
        return null;

    }
}

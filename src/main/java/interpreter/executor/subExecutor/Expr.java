package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.Env;
import interpreter.intermediate.node.INode;

public class Expr extends BaseExecutor {

    public Expr(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.EXPR)) {
            System.out.println("parse error");
        }

        return null;
    }
}

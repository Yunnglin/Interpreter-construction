package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.env.Env;
import interpreter.executor.signal.ForceExitSIgnal;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;

public class ExtrenDeclaration extends BaseExecutor {

    public ExtrenDeclaration(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal, ForceExitSIgnal {
        // a declare statement or a function definition
        if (!root.getSymbol().equals(LALRNonterminalSymbol.EXTERN_DECLARATION)) {
            throw new Exception("parse error in external declaration at line " +
                    root.getAttribute(INode.INodeKey.LINE));
        }

        return executeNode(root.getChild(0));
    }
}

package interpreter.executor.subExecutor;

import interpreter.debugger.StepFlag;
import interpreter.executor.BaseExecutor;
import interpreter.env.Env;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;

public class ExprStmt extends BaseExecutor {
    public ExprStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal {
        // expression ;
        if (!root.getSymbol().equals(LALRNonterminalSymbol.EXPR_STMT)) {
            throw new Exception("parse error in expression statement at line " +
                    root.getAttribute(INode.INodeKey.LINE));
        }
        // responsible for function call
        // check debugger's step flag
        StepFlag stepFlag = env.getCurStepFlag();
        if (env.isOnDebug() && stepFlag.equals(StepFlag.STEP_OVER)) {
            // step over, ignore the debug flag until the expression statement is done
            env.setOnDebug(false);
            Object exeResult = executeNode(root.getChild(0));
            env.setOnDebug(true);
            return exeResult;
        }

        // debug off, execute the expression
        return executeNode(root.getChild(0));
    }
}

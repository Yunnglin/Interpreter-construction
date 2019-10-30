package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;

public class WhileStmt extends BaseExecutor {
    public WhileStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.WHILE_STMT)) {
            System.out.println("parse error in while stmt");

        }
        //while-stmt -> while(expr) compound-stmt
        else {
            ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
            Executor exeExpr = factory.getExecutor(root.getChild(2), env);
            Executor exeCompound = factory.getExecutor(root.getChild(4), env);
            Object[] exeValue1 = (Object[]) exeExpr.Execute(root.getChild(2));
            while ((boolean)exeValue1[1])
            {
                Object[] exeValue2 = (Object[]) exeCompound.Execute(root.getChild(4));
                exeValue1 = (Object[]) exeExpr.Execute(root.getChild(2));
            }

        }
        return null;
    }
}

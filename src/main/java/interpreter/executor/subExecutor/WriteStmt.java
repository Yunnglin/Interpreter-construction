package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;

public class WriteStmt extends BaseExecutor {
    public WriteStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.WRITE_STMT)) {
            System.out.println("parse error in write stmt");

        }
        //write-stmt ->write expr ;
        else {
            ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
            Executor exeExpr = factory.getExecutor(root.getChild(1), env);
            Object[] exeValue1 = (Object[]) exeExpr.Execute(root.getChild(1));
            String writeString = (String) exeValue1[1];

        }
        return null;
    }
}


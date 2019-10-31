package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.exception.ExecutionError;
import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.TypeForm;

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
            DataType resultType = (DataType) exeValue1[0];
            //如果表达式的返回值可以转为boolean
            if (env.whileCompatible(resultType)) {
                while ((double)exeValue1[1]!=0) {
                    Object[] exeValue2 = (Object[]) exeCompound.Execute(root.getChild(4));
                    exeValue1 = (Object[]) exeExpr.Execute(root.getChild(2));
                }
            }
            //不可则抛出错误
            else
            {
                throw ExecutionError.newWhileUnmatchTypeError(resultType,
                        (Integer) root.getChild(2).getAttribute(INode.INodeKey.LINE));
            }

        }
        return null;
    }
}

package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.exception.ExecutionError;
import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.executor.signal.ForceExitSIgnal;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.TypeForm;

public class WhileStmt extends BaseExecutor {
    public WhileStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal, ForceExitSIgnal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.WHILE_STMT)) {
            throw new Exception("parse error in while stmt");
        }
        //while-stmt -> while(expr) compound-stmt
        else {
            // set current line if in debug mode
            env.setCurDebugLine((Integer) root.getAttribute(INode.INodeKey.LINE));
            Object[] exeValue1 = (Object[]) executeNode(root.getChild(2));
            DataType resultType = (DataType) exeValue1[0];
            //如果表达式的返回值可以转为boolean
            if (env.getTypeSystem().whileCompatible(resultType)) {
                if (Double.valueOf(exeValue1[1].toString())!=0) {
                    Object[] exeValue2 = (Object[]) executeNode(root.getChild(4));
                    executeNode(root);
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

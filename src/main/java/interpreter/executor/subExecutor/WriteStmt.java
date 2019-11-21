package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.exception.ExecutionError;
import interpreter.exception.SemanticError;
import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.executor.signal.ForceExitSIgnal;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.type.BasicType;
import interpreter.intermediate.type.DataType;
import message.Message;

public class WriteStmt extends BaseExecutor {
    public WriteStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal, ForceExitSIgnal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.WRITE_STMT)) {
            throw new Exception("parse error in write stmt");
        }
        //write-stmt ->write expr ;
        else {
            ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
            Executor exeExpr = factory.getExecutor(root.getChild(1), env);
            Object[] exeValue1 = (Object[]) exeExpr.Execute(root.getChild(1));
            DataType resultType=(DataType) exeValue1[0];
            //如果表达式返回值可以被write
            if(env.getTypeSystem().writeCompatible(resultType))
            {
                Message message = new Message(Message.MessageType.WRITE, exeValue1[1]);
                postMessage(message);
            }
            //不可则报类型不匹配错误
            else
            {
                throw ExecutionError.newWriteUnmatchTypeError(resultType,
                        (Integer) root.getChild(1).getAttribute(INode.INodeKey.LINE));
            }
        }
        return null;
    }
}


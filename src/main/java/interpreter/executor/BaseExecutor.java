package interpreter.executor;

import interpreter.env.Env;
import interpreter.executor.signal.ForceExitSIgnal;
import interpreter.executor.subExecutor.ReturnStmt;
import interpreter.intermediate.node.INode.INodeKey;
import interpreter.intermediate.node.INode;
import message.Message;

public abstract class BaseExecutor implements Executor {

    protected Env env;

    public BaseExecutor(Env env) {
        this.env = env;
    }

    protected Object executeNode(INode root) throws Exception, ReturnStmt.ReturnSignal, ForceExitSIgnal {
        // set current line if in debug mode
//        int i = 0;
//
//        while (i < root.getChildren().size()) {
//            INode child = root.getChildren().get(i);
//            Executor executor = factory.getExecutor(child,env);
//            executor.Execute(child);
//            i++;
//        }
        Executor executor = getSpecExecutor(root);

        return executor.Execute(root);
    }

    protected void copyChild(INode root, INode child) {
        root.setAttribute(INode.INodeKey.LINE, child.getAttribute(INodeKey.LINE));
    }

    protected Executor getSpecExecutor(INode node) {
        ExecutorFactory executorFactory = ExecutorFactory.getExecutorFactory();
        return executorFactory.getExecutor(node, env);
    }

    protected void postMessage(Message message) {
        env.sendMessage(message);
    }

}

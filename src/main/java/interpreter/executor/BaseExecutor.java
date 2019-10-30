package interpreter.executor;

import interpreter.exception.SemanticError;
import interpreter.intermediate.Env;
import interpreter.intermediate.node.INode.INodeKey;
import interpreter.intermediate.node.INode;

public abstract class BaseExecutor implements Executor {

    protected Env env;

    public BaseExecutor(Env env) {
        this.env = env;
    }

    protected Object executeNode(INode root) throws Exception {
        ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
//        int i = 0;
//
//        while (i < root.getChildren().size()) {
//            INode child = root.getChildren().get(i);
//            Executor executor = factory.getExecutor(child,env);
//            executor.Execute(child);
//            i++;
//        }
        Executor executor = factory.getExecutor(root, env);

        return executor.Execute(root);
    }

    protected void copyChild(INode root, INode child) {
        root.setAttribute(INode.INodeKey.LINE, child.getAttribute(INodeKey.LINE));
    }

    protected Executor getSpecExecutor(INode node) {
        ExecutorFactory executorFactory = ExecutorFactory.getExecutorFactory();
        return executorFactory.getExecutor(node, env);
    }

}

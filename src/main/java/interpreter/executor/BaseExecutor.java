package interpreter.executor;

import interpreter.intermediate.node.INode.INodeKey;
import interpreter.intermediate.node.INode;

public abstract class BaseExecutor implements Executor {
    protected void executeChildNode(INode root) {
        ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
//        int i = 0;
//        while (i < root.getChildren().size()) {
//            INode child = root.getChildren().get(i);
//            Executor executor = factory.getExecutor(child);
//            executor.Execute(child);
//            i++;
//        }
        Executor executor = factory.getExecutor(root);
        executor.Execute(root);
    }

    protected void copyChild(INode root, INode child) {
        root.setAttribute(INode.INodeKey.LINE, child.getAttribute(INodeKey.LINE));
    }

}

package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;

public class Stmt extends BaseExecutor {

    public Stmt(Env env){
        super(env);
    }
    @Override
    public Object Execute(INode root) throws Exception {
//        Executor executor = getSpecExecutor(root.getChild(0));
//        executor.Execute(root.getChild(0));
       return executeNode(root.getChild(0));

    }
}

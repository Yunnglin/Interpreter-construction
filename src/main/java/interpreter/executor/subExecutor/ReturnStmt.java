package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.executor.BaseExecutor;
import interpreter.intermediate.node.INode;

public class ReturnStmt extends BaseExecutor {
    public ReturnStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        return null;
    }
}

package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;

public class ExprStmt extends BaseExecutor {
    public ExprStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        return null;
    }
}

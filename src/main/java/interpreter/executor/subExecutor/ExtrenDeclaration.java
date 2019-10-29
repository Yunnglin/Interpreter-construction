package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.intermediate.Env;
import interpreter.intermediate.node.INode;

public class ExtrenDeclaration extends BaseExecutor {

    public ExtrenDeclaration(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        return null;
    }
}

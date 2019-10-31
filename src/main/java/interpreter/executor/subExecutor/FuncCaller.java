package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTblEntry;

public class FuncCaller {

    public static FuncCaller instance;

    public FuncCaller getInstance() {
        return instance;
    }

    private FuncCaller() {}

    public Object callFunc(Env env, String funcName, INode[] paramsVals) {
        SymTblEntry funcEntry = env.findSymTblEntry(funcName);

        return null;
    }

}

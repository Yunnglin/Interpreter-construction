package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.exception.SemanticError;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.DataType;

public class FuncCaller {

    public static FuncCaller instance;

    public FuncCaller getInstance() {
        return instance;
    }

    private FuncCaller() {}

    public Object callFunc(Env env, String funcName, INode[] paramsVals, INode caller) throws SemanticError {
        SymTblEntry funcEntry = env.findSymTblEntry(funcName);

        // check the name is a function name
        DataType type = (DataType) funcEntry.getValue(SymTbl.SymTblKey.TYPE);
        if (!type.equals(DataType.PredefinedType.TYPE_FUNC)) {
            throw SemanticError.newSymbolNotCallableError(funcName,
                    (Integer) caller.getAttribute(INode.INodeKey.LINE));
        }

        SymTbl symtbl = (SymTbl) funcEntry.getValue(SymTbl.SymTblKey.SYMTBL);
        INode[] body = (INode[]) funcEntry.getValue(SymTbl.SymTblKey.FUNC_BODY);

        return null;
    }

}

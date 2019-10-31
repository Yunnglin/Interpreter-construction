package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.exception.ExecutionError;
import interpreter.exception.SemanticError;
import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.FuncPrototype;

import java.util.*;

public class FuncCaller extends BaseExecutor {

    public FuncCaller(Env env) {
        super(env);
    }

    public Object[] callFunc(String funcName, INode[] paramsVals, INode caller) throws Exception, ReturnStmt.ReturnSignal {
        SymTblEntry funcEntry = env.findSymTblEntry(funcName);

        // check the name is a function name
        DataType type = (DataType) funcEntry.getValue(SymTbl.SymTblKey.TYPE);
        if (!type.equals(DataType.PredefinedType.TYPE_FUNC)) {
            throw SemanticError.newSymbolNotCallableError(funcName,
                    (Integer) caller.getAttribute(INode.INodeKey.LINE));
        }

        // get the definition of function
        SymTbl symtbl = (SymTbl) funcEntry.getValue(SymTbl.SymTblKey.SYMTBL);
        FuncPrototype prototype = (FuncPrototype) funcEntry.getValue(SymTbl.SymTblKey.FUNC_PROTOTYPE);
        INode[] body = (INode[]) funcEntry.getValue(SymTbl.SymTblKey.FUNC_BODY);
        DataType[] paramTypes = prototype.getParamTypes();
        Set<Map.Entry<String, SymTblEntry>> paramList = symtbl.getEntries().entrySet();


        // check amount of params
        if (paramTypes.length != paramsVals.length) {
            throw ExecutionError.newFewManyArgsError(funcName, paramTypes.length > paramsVals.length,
                    (Integer) caller.getAttribute(INode.INodeKey.LINE));
        }


        Iterator<Map.Entry<String, SymTblEntry>> iterator = paramList.iterator();
        // push each value into symbol table
        for (int i=paramsVals.length-1; i>=0; --i) {
            Object[] paramVal = (Object[]) executeNode(paramsVals[i]);
            DataType valType = (DataType) paramVal[0];
            if (!env.initializeCompatible(paramTypes[i], valType)) {
                throw ExecutionError.newArgTypeNotCompatible(funcName, paramTypes[i], valType, i,
                        (Integer) caller.getAttribute(INode.INodeKey.LINE));
            }
            // pass value to parameter
            Map.Entry<String, SymTblEntry> next = iterator.next();
            SymTblEntry paramEntry = next.getValue();
            paramEntry.addValue(SymTbl.SymTblKey.VALUE, paramVal[1]);
        }

        // execute the function
        env.pushSymTbl(symtbl);
        try {
            for (INode stmt : body) {
                executeNode(stmt);
            }
        } catch (ReturnStmt.ReturnSignal returnSignal) {
            return returnSignal.getRetValue();
        } finally {
            env.popSymTbl();
        }

        // no return statement executed
        if (prototype.getRetType().equals(DataType.PredefinedType.TYPE_VOID)) {
            return new Object[] {DataType.PredefinedType.TYPE_VOID, null};
        }

        throw ExecutionError.newMissingReturnError(funcName, (Integer) caller.getAttribute(INode.INodeKey.LINE));
    }

    @Override
    public Object Execute(INode root) throws Exception {
        return null;
    }
}

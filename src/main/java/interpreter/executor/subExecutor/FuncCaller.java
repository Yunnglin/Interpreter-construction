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

    /**
     * help caller call a function in current execution env
     * @param funcName String: the name of function
     * @param paramsVals INode[]: the values of params passing to function
     * @param caller INode: the symbol node that calls the function
     * @return the return value of this call
     * @throws Exception the exception thrown in the process of execution
     * @throws ReturnStmt.ReturnSignal a unexpected case, the return signal that escaped from catching
     */
    public Object[] callFunc(String funcName, INode[] paramsVals, INode caller) throws Exception, ReturnStmt.ReturnSignal {
        SymTblEntry funcEntry = env.findSymTblEntry(funcName);

        // check the name is a function name
        DataType type = (DataType) funcEntry.getValue(SymTbl.SymTblKey.TYPE);
        if (!type.equals(DataType.PredefinedType.TYPE_FUNC)) {
            throw SemanticError.newSymbolNotCallableError(funcName,
                    (Integer) caller.getAttribute(INode.INodeKey.LINE));
        }

        // get the definition of function
        SymTbl symtbl = ((SymTbl) funcEntry.getValue(SymTbl.SymTblKey.SYMTBL)).copy();
        FuncPrototype prototype = (FuncPrototype) funcEntry.getValue(SymTbl.SymTblKey.FUNC_PROTOTYPE);
        INode[] body = (INode[]) funcEntry.getValue(SymTbl.SymTblKey.FUNC_BODY);
        DataType[] paramTypes = prototype.getParamTypes();
        Set<Map.Entry<String, SymTblEntry>> paramList = symtbl.getEntries().entrySet();


        // check amount of params
        if (paramTypes.length != paramsVals.length) {
            throw ExecutionError.newFewManyArgsError(funcName, paramTypes.length > paramsVals.length,
                    (Integer) caller.getAttribute(INode.INodeKey.LINE));
        }


        ListIterator<Map.Entry<String, SymTblEntry>> iterator =
                new ArrayList<>(paramList).listIterator(paramList.size());
        // push each value into symbol table
        for (int i=paramsVals.length-1; i>=0; --i) {
            Object[] paramVal = (Object[]) executeNode(paramsVals[i]);
            DataType valType = (DataType) paramVal[0];
            if (!env.getTypeSystem().initializeCompatible(paramTypes[i], valType)) {
                throw ExecutionError.newArgTypeNotCompatible(funcName, paramTypes[i], valType, i,
                        (Integer) caller.getAttribute(INode.INodeKey.LINE));
            }
            // pass value to parameter
            Map.Entry<String, SymTblEntry> previous = iterator.previous();
            SymTblEntry paramEntry = previous.getValue();
            paramEntry.addValue(SymTbl.SymTblKey.VALUE, paramVal[1]);
        }

        // return value
        Object[] retValue = null;
        int retLine = 0;
        // execute the function
        env.pushSymTbl(symtbl);
        try {
            for (INode stmt : body) {
                executeNode(stmt);
            }
        } catch (ReturnStmt.ReturnSignal returnSignal) {
            // return ret value
            retValue = returnSignal.getRetValue();
            retLine = returnSignal.getLine();
        } finally {
            env.popSymTbl();
        }

        // no return statement executed
        if (prototype.getRetType().equals(DataType.PredefinedType.TYPE_VOID)) {
            retValue = new Object[] {DataType.PredefinedType.TYPE_VOID, null};
        } else if (funcEntry.equals(env.getMainEntry())) {
            retValue = new Object[] {DataType.PredefinedType.TYPE_INT, 0};
        }

        if (retValue == null) {
            throw ExecutionError.newMissingReturnError(funcName,
                    (Integer) caller.getAttribute(INode.INodeKey.LINE));
        } else {
            if (env.getTypeSystem().returnCompatible(prototype.getRetType(), (DataType) retValue[0])) {
                return retValue;
            } else {
                throw SemanticError.newReturnIncompatibleTypeError(funcName, prototype.getRetType(),
                        (DataType) retValue[0], retLine);
            }
        }

    }

    @Override
    public Object Execute(INode root) throws Exception {
        return null;
    }
}

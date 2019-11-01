package interpreter.env;

import interpreter.exception.ExecutionError;
import interpreter.exception.SemanticError;
import interpreter.executor.subExecutor.FuncCaller;
import interpreter.executor.subExecutor.ReturnStmt;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.type.FuncPrototype;
import message.Message;
import message.MessageHandler;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.BasicType;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.TypeForm;
import message.MessageListener;
import message.MessageProducer;

import java.util.ArrayList;
import java.util.Stack;

public class Env implements MessageProducer {

    public static String mainEntryName = "main";

    private Stack<SymTbl> symTblStack;
    private int curNestingLevel;
    private MessageHandler ioHandler;
    private SymTblEntry mainEntry;

    public Env() {
        symTblStack = new Stack<SymTbl>();
        curNestingLevel = 0;
        symTblStack.push(new SymTbl(curNestingLevel));
        ioHandler = new MessageHandler();
    }

    /**
     * push a new symbol table into the stack of symbol tables
     *
     * @param table
     */
    public void pushSymTbl(SymTbl table) {
        symTblStack.push(table);
        curNestingLevel++;
        table.setNestingLevel(curNestingLevel);
    }

    /**
     * pop the top table out of the stack of symbol tables
     *
     * @return the popped item
     */
    public SymTbl popSymTbl() {
        curNestingLevel--;
        return symTblStack.pop();
    }

    /**
     * find a symbol table entry in available scope
     *
     * @param name the name of the entry
     * @return the entry found or null if the entry is not in all tables
     */
    public SymTblEntry findSymTblEntry(String name) {
        SymTblEntry entry;
        for (int i = curNestingLevel; i >= 0; i--) {
            if ((entry = symTblStack.get(i).find(name)) != null) {
                return entry;
            }
        }

        return null;
    }

    public int getCurNestingLevel() {
        return curNestingLevel;
    }

    public SymTbl getCurScopeSymTbl() {
        return this.symTblStack.get(curNestingLevel);
    }

    public boolean assignCompatible(DataType target, DataType value) {
        if (target.getBasicType().equals(BasicType.VOID)
                || value.getBasicType().equals(BasicType.VOID)) {
            return false;
        }

        if ((target.equals(value)) && (target.getForm().equals(TypeForm.SCALAR))) {
            return true;
        } else if ((target.equals(DataType.PredefinedType.TYPE_REAL))
                && (value.equals(DataType.PredefinedType.TYPE_INT))) {
            return true;
        }

        return false;
    }

    public boolean writeCompatible(DataType test) {
        if (test.getBasicType().equals(BasicType.VOID))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    public boolean whileCompatible(DataType test) {
        if (test.getForm().equals(TypeForm.SCALAR)&&!test.getBasicType().equals(BasicType.VOID))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean compareCompatible(DataType type1, DataType type2) {
        if (type1.equals(DataType.PredefinedType.TYPE_VOID)
                || type2.equals(DataType.PredefinedType.TYPE_VOID)) {
            return false;
        }

        if (type1.getForm().equals(TypeForm.SCALAR) && type2.getForm().equals(TypeForm.SCALAR)) {
            // one integer and one real number
            if ((type1 == type2)
                    || (type1.equals(DataType.PredefinedType.TYPE_INT) &&
                    type2.equals(DataType.PredefinedType.TYPE_REAL))
                    || (type1.equals(DataType.PredefinedType.TYPE_REAL) &&
                    type2.equals(DataType.PredefinedType.TYPE_INT))) {
                return true;
            }
        }

        return false;
    }

    public boolean initializeCompatible(DataType target, DataType value) {
        boolean compatible = assignCompatible(target, value);
        if (compatible) {
            // can assign to target
            return true;
        } else if (target.getForm().equals(TypeForm.ARRAY)
                && value.getForm().equals(TypeForm.ARRAY)) {
            // array initialization
            DataType targetScalar = new DataType(target.getBasicType(), TypeForm.SCALAR);
            DataType valueScalar = new DataType(value.getBasicType(), TypeForm.SCALAR);

            return assignCompatible(targetScalar, valueScalar);
        }

        return false;
    }

    @Override
    public void addMessageListener(MessageListener listener) {
        this.ioHandler.addListener(listener);
    }

    @Override
    public void removeMessageListener(MessageListener listener) {
        this.ioHandler.removeListener(listener);
    }

    @Override
    public void sendMessage(Message message) {
        this.ioHandler.sendMessage(message);
    }

    public DataType getBasicDataType(String type) {
        BasicType basicType = null;
        if ( (basicType = BasicType.getBasicType(type)) != null) {
            return new DataType(basicType, TypeForm.SCALAR);
        }

        return null;
    }

    public boolean defaultInitializer(SymTblEntry entry) {
        DataType type = (DataType) entry.getValue(SymTbl.SymTblKey.TYPE);

        if (type.getForm().equals(TypeForm.SCALAR)) {
            // single value of basic type
            entry.addValue(SymTbl.SymTblKey.VALUE, defaultBasicInitializer(type.getBasicType()));

            return true;
        } else if (type.getForm().equals(TypeForm.ARRAY)) {
            // array initializing
            Integer size = (Integer) entry.getValue(SymTbl.SymTblKey.ARRAY_SIZE);
            ArrayList<Object> values = new ArrayList<>();
            for (int i=0; i<size; ++i) {
                values.add(defaultBasicInitializer(type.getBasicType()));
            }
            entry.addValue(SymTbl.SymTblKey.VALUE, values.toArray());

            return true;
        }

        return false;
    }

    private Object defaultBasicInitializer(BasicType basicType) {
        if (basicType.equals(BasicType.INT)) {
            return 0;
        } else if (basicType.equals(BasicType.REAL)) {
            return 0.0;
        } else if (basicType.equals(BasicType.VOID)) {
            return null;
        }

        return null;
    }

    public void setMainEntry(SymTblEntry entry) {
        FuncPrototype mainFuncProto = (FuncPrototype) entry.getValue(SymTbl.SymTblKey.FUNC_PROTOTYPE);
        DataType type = (DataType) entry.getValue(SymTbl.SymTblKey.TYPE);

        // main should be a function
        if (!type.equals(DataType.PredefinedType.TYPE_FUNC)) {
            return;
        }
//        DataType retType = mainFuncProto.getRetType();
//        // the return type of main should be void or int
//        if (!(retType.equals(DataType.PredefinedType.TYPE_INT) ||
//                retType.equals(DataType.PredefinedType.TYPE_VOID))) {
//
//        }
        this.mainEntry = entry;
    }

    public Integer runProgram() {
        Message message = new Message();
        int statusCode = 0;
        if (mainEntry == null) {
            message.setType(Message.MessageType.EXECUTION_ERROR);
            message.setBody("PROGRAM ENTRY '" + Env.mainEntryName + "' FUNCTION NOT FOUND");
        } else {
            INode callerNode = new INode(LALRNonterminalSymbol.E);
            callerNode.setAttribute(INode.INodeKey.LINE, 0);
            // call main
            FuncCaller caller = new FuncCaller(this);
            try {
                long exeStartTime = System.currentTimeMillis();
                caller.callFunc(mainEntry.getName(), new INode[0], callerNode);
                float exeElapsedTime = (System.currentTimeMillis() - exeStartTime) / 1000f;
                statusCode = 0;
                Object[] exeMsgBody = new Object[] {exeElapsedTime, statusCode};
                message.setType(Message.MessageType.INTERPRETER_SUMMARY);
                message.setBody(exeMsgBody);
            } catch (SemanticError se) {
                System.out.println(se);
                message.setType(Message.MessageType.SEMANTIC_ERROR);
                message.setBody(se);
                statusCode = -1;
            } catch (ExecutionError ee) {
                System.out.println(ee);
                message.setType(Message.MessageType.EXECUTION_ERROR);
                message.setBody(ee);
                statusCode = -1;
            } catch (Exception | ReturnStmt.ReturnSignal e) {
                e.printStackTrace();
                System.out.println(e);
                message.setType(Message.MessageType.INTERPRETER_SUMMARY);
                message.setBody(new Object[]{0, -1});
                statusCode = -1;
            } finally {
                sendMessage(message);
                System.out.println(message.toString());
            }
        }

        return statusCode;
    }

}

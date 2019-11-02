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

    public static String defaultMainEntranceName = "main";

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

    /**
     * get the current nesting level of symbol table
     * @return int, represents the nesting level, starting from 0
     */
    public int getCurNestingLevel() {
        return curNestingLevel;
    }

    /**
     * get the symbol table for current scope
     * @return the symbol table in current scope
     */
    public SymTbl getCurScopeSymTbl() {
        return this.symTblStack.get(curNestingLevel);
    }

    /**
     * get the main entrance symbol table entry of the program
     * @return a symbol table entry that records the entrance function
     */
    public SymTblEntry getMainEntry() {
        return mainEntry;
    }

    /**
     * check the whether the assignment is compatible
     * @param target the type of assignment target
     * @param value the type of assignment value
     * @return whether the assignment compatible
     */
    public boolean assignCompatible(DataType target, DataType value) {
        // if void exists, absolutely incompatible
        if (target.getBasicType().equals(BasicType.VOID)
                || value.getBasicType().equals(BasicType.VOID)) {
            return false;
        }

        if ((target.equals(value)) && (target.getForm().equals(TypeForm.SCALAR))) {
            // the same type in scalar form is compatible
            return true;
        } else if ((target.equals(DataType.PredefinedType.TYPE_REAL))
                && (value.equals(DataType.PredefinedType.TYPE_INT))) {
            // int(4) can be cast to real(8) implicitly
            return true;
        }

        return false;
    }

    /**
     * check whether the return value is compatible to prototype
     * @param proto the type of return value defined in prototype
     * @param value the type of return value in execution
     * @return
     */
    public boolean returnCompatible(DataType proto, DataType value) {
        if (proto.equals(value)) {
            // the same type as which defined in function prototype
            return true;
        } else {
            // otherwise, check whether the two types can do assignment
            return assignCompatible(proto, value);
        }
    }

    /**
     * check whether the value can be written
     * @param test the type of value to write
     * @return whether the type is legal
     */
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

    /**
     * check whether the value can be placed in condition
     * @param test the type of the value that to be condition
     * @return whether the value can be condition
     */
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

    /**
     * check whether two values can do comparison
     * @param type1 type of value1
     * @param type2 type of value2
     * @return whether value1 and value1 can compare
     */
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

    /**
     * check whether the initialization is compatible according to the type declared
     * @param target the type of declared symbol
     * @param value the type of initial value
     * @return whether value can used to initialize target
     */
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

    /**
     * add a listener to the current component env(responsible for IO)
     * @param listener
     */
    @Override
    public void addMessageListener(MessageListener listener) {
        this.ioHandler.addListener(listener);
    }

    /**
     * remove a listener from the env if it exists in the list
     * @param listener
     */
    @Override
    public void removeMessageListener(MessageListener listener) {
        this.ioHandler.removeListener(listener);
    }

    /**
     * send a message to the listeners for current component
     * @param message
     */
    @Override
    public void sendMessage(Message message) {
        this.ioHandler.sendMessage(message);
    }

    /**
     * get the basic data type according to the string of basic type
     * @param type the string of basic type
     * @return the corresponding data type
     */
    public DataType getBasicDataType(String type) {
        BasicType basicType = null;
        if ( (basicType = BasicType.getBasicType(type)) != null) {
            return new DataType(basicType, TypeForm.SCALAR);
        }

        return null;
    }

    /**
     * get the basic data type according to a basic type
     * @param basicType the basic type
     * @return the corresponding data type
     */
    public DataType getBasicDataType(BasicType basicType) {
        return new DataType(basicType, TypeForm.SCALAR);
    }

    /**
     * set the value to be default value according to the type
     * @param entry the entry of the symbol to initialize
     */
    public void defaultInitializer(SymTblEntry entry) {
        // get the type
        DataType type = (DataType) entry.getValue(SymTbl.SymTblKey.TYPE);

        if (type.getForm().equals(TypeForm.SCALAR)) {
            // single value of basic type
            entry.addValue(SymTbl.SymTblKey.VALUE, defaultBasicInitializer(type.getBasicType()));

        } else if (type.getForm().equals(TypeForm.ARRAY)) {
            // array initializing
            Integer size = (Integer) entry.getValue(SymTbl.SymTblKey.ARRAY_SIZE);
            ArrayList<Object> values = new ArrayList<>();
            for (int i=0; i<size; ++i) {
                values.add(defaultBasicInitializer(type.getBasicType()));
            }
            entry.addValue(SymTbl.SymTblKey.VALUE, values.toArray());

        }

    }

    /**
     * return the default value for a specific basic type
     * @param basicType the basic type
     * @return the default value for the basic type
     */
    private Object defaultBasicInitializer(BasicType basicType) {
        if (basicType.equals(BasicType.INT)) {
            // integer, 0
            return 0;
        } else if (basicType.equals(BasicType.REAL)) {
            // real, 0.0
            return 0.0;
        } else if (basicType.equals(BasicType.VOID)) {
            // void, null
            return null;
        } else if (basicType.equals(BasicType.FUNC_POINTER)) {
            // TODO function pointer
        } else if (basicType.equals(BasicType.POINTER)) {
            // TODO pointer
        }

        return null;
    }

    /**
     * set the entry of main entrance
     * @param entry the symbol entry registered for the entrance function
     */
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

//    public Integer runProgram() {
//        Message message = new Message();
//        int statusCode = 0;
//        if (mainEntry == null) {
//            message.setType(Message.MessageType.EXECUTION_ERROR);
//            message.setBody("PROGRAM ENTRY '" + Env.defaultMainEntranceName + "' FUNCTION NOT FOUND");
//        } else {
//            INode callerNode = new INode(LALRNonterminalSymbol.E);
//            callerNode.setAttribute(INode.INodeKey.LINE, 0);
//            // call mainj
//            FuncCaller caller = new FuncCaller(this);
//            try {
//                long exeStartTime = System.currentTimeMillis();
//                caller.callFunc(mainEntry.getName(), new INode[0], callerNode);
//                float exeElapsedTime = (System.currentTimeMillis() - exeStartTime) / 1000f;
//                statusCode = 0;
//                Object[] exeMsgBody = new Object[] {exeElapsedTime, statusCode};
//                message.setType(Message.MessageType.INTERPRETER_SUMMARY);
//                message.setBody(exeMsgBody);
//            } catch (SemanticError se) {
//                System.out.println(se);
//                message.setType(Message.MessageType.SEMANTIC_ERROR);
//                message.setBody(se);
//                statusCode = -1;
//            } catch (ExecutionError ee) {
//                System.out.println(ee);
//                message.setType(Message.MessageType.EXECUTION_ERROR);
//                message.setBody(ee);
//                statusCode = -1;
//            } catch (Exception | Error | ReturnStmt.ReturnSignal e) {
//                e.printStackTrace();
//                System.out.println(e);
//                message.setType(Message.MessageType.INTERPRETER_SUMMARY);
//                message.setBody(new Object[]{0, -1});
//                statusCode = -1;
//            } finally {
//                sendMessage(message);
//                System.out.println(message.toString());
//            }
//        }
//
//        return statusCode;
//    }

}

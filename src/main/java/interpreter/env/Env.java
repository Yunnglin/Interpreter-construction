package interpreter.env;

import interpreter.debugger.Breakpoint;
import interpreter.debugger.Debugger;
import interpreter.debugger.StepFlag;
import interpreter.intermediate.type.FuncPrototype;
import message.Message;
import message.MessageHandler;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.DataType;
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
    private TypeSystem typeSystem;

    private boolean onDebug;
    private Debugger debugger;

    public Env() {
        symTblStack = new Stack<SymTbl>();
        curNestingLevel = 0;
        symTblStack.push(new SymTbl(curNestingLevel));
        ioHandler = new MessageHandler();
        typeSystem = new TypeSystem();
        debugger = null;
        onDebug = false;
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

    public TypeSystem getTypeSystem() {
        return typeSystem;
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

    /**
     * get the debug status
     * @return boolean
     */
    public boolean isOnDebug() {
        return onDebug;
    }

    /**
     * set the debug status
     * @param isOn boolean
     */
    public void setOnDebug(boolean isOn) {
        this.onDebug = isOn;
    }

    /**
     * initialize debugger with breakpoints
     * @param breakpoints ArrayList of Breakpoint
     */
    public void initDebugger(ArrayList<Breakpoint> breakpoints) {
        this.onDebug = true;
        this.debugger = new Debugger(breakpoints);
    }

    public void setCurDebugLine(int line) {
        if (this.onDebug) {
            this.debugger.setCurLine(line);
        }
    }

    /**
     * get the current step flag (step in, step over, step out, off)
     * @return StepFlag
     */
    public StepFlag getCurStepFlag() {
        if (this.onDebug) {
            return this.debugger.getStepFlag();
        }

        return null;
    }

    /**
     * check whether the execution of specified line should be stopped
     * @param line int, the number of line
     * @return boolean
     */
    public boolean shouldStopExecution(int line) {
        if (!onDebug)
            return false;

        StepFlag stepFlag = this.debugger.getStepFlag();
        return this.onDebug && (
                (stepFlag.equals(StepFlag.OFF) && this.debugger.shouldBreak(line)) ||
                        stepFlag.equals(StepFlag.STEP_IN) ||
                        stepFlag.equals(StepFlag.STEP_OVER)
                );
    }

    /**
     * stop the current execution by suspend the execution thread
     * @param line int, the number of the line where the statement is at
     * @throws InterruptedException
     */
    public void stopCurExecution(int line) throws InterruptedException {
        if (onDebug)
            this.debugger.stopCurExecution(line);
    }

    public void stepIn() {
        if (onDebug) {
            this.debugger.stepIn();
        }
    }

    public void stepOver() {
        if (onDebug) {
            this.debugger.stepOver();
        }
    }

    public void continueExecution() {
        if (onDebug) {
            this.debugger.continueExecution();
        }
    }

    public boolean addBreakpoint(Breakpoint breakpoint) {
        if (onDebug)
            return this.debugger.addBreakpoint(breakpoint);

        return false;
    }

    public boolean removeBreakpoint(Breakpoint breakpoint) {
        if (onDebug)
            return this.debugger.removeBreakpoint(breakpoint);

        return false;
    }
}

package interpreter.debugger;

import java.util.ArrayList;

public class Debugger {

    private ArrayList<Breakpoint> breakpoints;
    private final Object debugLock;
    private Integer curLine;

    private StepFlag stepFlag;

    public Debugger(ArrayList<Breakpoint> breakpoints) {
        this.breakpoints = breakpoints;
        this.debugLock = new Object();
        this.curLine = 0;
        this.stepFlag = StepFlag.OFF;
        this.curLine = null;
    }

    public Debugger() {
        this(new ArrayList<Breakpoint>());
    }

    public StepFlag getStepFlag() {
        return stepFlag;
    }

    public void stepOver() {
        this.stepFlag = StepFlag.STEP_OVER;
        synchronized (debugLock) {
            debugLock.notifyAll();
        }
    }

    public void stepIn() {
        this.stepFlag = StepFlag.STEP_IN;
        synchronized (debugLock) {
            debugLock.notifyAll();
        }

    }

    public void continueExecution() {
        this.stepFlag = StepFlag.OFF;
        synchronized (debugLock) {
            debugLock.notifyAll();
        }
    }

    public boolean addBreakpoint(Breakpoint breakpoint) {
        if (this.breakpoints.contains(breakpoint)) {
            return false;
        }

        return this.breakpoints.add(breakpoint);
    }

    public boolean removeBreakpoint(Breakpoint breakpoint) {
        return this.breakpoints.remove(breakpoint);
    }

    public boolean shouldBreak(int line) {
        // if current line encounters a breakpoint
        // and there is no other execution stopped by it before in this line.
        return (curLine == null || line != curLine) && encounterBreakpoint(line) != null;

    }

    private Breakpoint encounterBreakpoint(int line) {
        for (Breakpoint p : breakpoints) {
            if (p.getLine() == line) {
                return p;
            }
        }

        return null;
    }

    public void stopCurExecution(int line) throws InterruptedException {
        setCurLine(line);
        synchronized (debugLock) {
            debugLock.wait();
        }
    }

    public void setCurLine(int line) {
        this.curLine = line;
    }

}

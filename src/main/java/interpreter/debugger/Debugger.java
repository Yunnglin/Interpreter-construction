package interpreter.debugger;

import java.util.ArrayList;

public class Debugger {

    private ArrayList<Breakpoint> breakpoints;
    private final Object debugLock;
    private int curLine;
    private Breakpoint lastBreakpoint;

    private StepFlag stepFlag;

    public Debugger(ArrayList<Breakpoint> breakpoints) {
        this.breakpoints = breakpoints;
        this.debugLock = new Object();
        this.curLine = 0;
        this.stepFlag = StepFlag.OFF;
        this.lastBreakpoint = null;
    }

    public Debugger() {
        this(new ArrayList<Breakpoint>());
    }

    public Object getDebugLock() {
        return debugLock;
    }

    public StepFlag getStepFlag() {
        return stepFlag;
    }

    private void stepOver() {
        this.stepFlag = StepFlag.STEP_OVER;
        synchronized (debugLock) {
            debugLock.notifyAll();
        }
    }

    private void stepIn() {
        this.stepFlag = StepFlag.STEP_IN;
        synchronized (debugLock) {
            debugLock.notifyAll();
        }

    }

    private void continueExecution() {
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
        return (lastBreakpoint == null || line != lastBreakpoint.getLine()) && encounterBreakpoint(line) != null;

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
        Breakpoint breakpoint = encounterBreakpoint(line);
        if (breakpoint != null) {
            lastBreakpoint = breakpoint;
        }
        synchronized (debugLock) {
            debugLock.wait();
        }
    }

}

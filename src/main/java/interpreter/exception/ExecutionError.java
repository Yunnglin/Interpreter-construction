package interpreter.exception;

public class ExecutionError extends InterpError {

    public ExecutionError(String msg, int line, ErrorCode code) {
        super(msg, line, code);
    }

    @Override
    public String getMessage() {
        return "Execution error (" + getCode() + ") at line "+this.getLine()+": "+this.getInnerMsg();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

    public static ExecutionError newDivByZeroError(int line) {
        return new ExecutionError("DIVISION BY ZERO", line, ErrorCode.DIV_BY_ZERO);
    }
}

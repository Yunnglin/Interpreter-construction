package interpreter.exception;

public class InterpError extends Exception {
    private int line;
    private ErrorCode code;

    public InterpError(String msg, int line, ErrorCode code) {
        super(msg);
        this.line = line;
        this.code = code;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getInnerMsg() {
        return super.getMessage();
    }

    public int getCode() {
        return this.code.getCode();
    }

    @Override
    public String getMessage() {
        return "Error at line "+this.line+": "+this.getInnerMsg();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}

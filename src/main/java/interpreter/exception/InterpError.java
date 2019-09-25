package interpreter.exception;

public class InterpError extends Exception {
    private int line;

    public InterpError(String msg, int line) {
        super(msg);
        this.line = line;
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

    @Override
    public String getMessage() {
        return "Error at line "+this.line+": "+this.getInnerMsg();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}

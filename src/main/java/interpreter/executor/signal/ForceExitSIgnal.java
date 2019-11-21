package interpreter.executor.signal;


public class ForceExitSIgnal extends Throwable {
    private int line;

    @Override
    public String getMessage() {
        return "Force Exit Signal At line " + line;
    }

    public ForceExitSIgnal(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}

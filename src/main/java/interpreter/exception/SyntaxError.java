package interpreter.exception;

public class SyntaxError extends InterpError {
    public SyntaxError(String msg, int line) {
        super(msg, line);
    }

    @Override
    public String getMessage() {
        return "Syntax Error at line "+this.getLine()+": "+this.getInnerMsg();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

    public static SyntaxError newLexicalError(String lex, int line) {
        return new SyntaxError("BAD CHARACTER "+lex, line);
    }
}

package interpreter.exception;

public class SemanticError extends InterpError {

    public SemanticError(String msg, int line, ErrorCode code) {
        super(msg, line, code);
    }

    @Override
    public String getMessage() {
        return "Semantic error (" + getCode() + ") at line "+this.getLine()+": "+this.getInnerMsg();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

    public static SemanticError newDupDeclareError(String lex, int newline, int oldline) {
        return new SemanticError("DUPLICATE DECLARATION FOR '" +
                lex + "' AT LINE " + oldline, newline, ErrorCode.DUP_DECLARATION);
    }

    public static SemanticError newNegativeArraySizeError(String lex, int line) {
        return new SemanticError("SIZE OF ARRAY '" + lex + "' IS NEGATIVE",
                line, ErrorCode.NEG_ARRAY_SIZE);
    }

    public static SemanticError newNonIntegerArraySizeError(String lex, int line) {
        return new SemanticError("SIZE OF ARRAY '" + lex + "' HAS NON-INTEGER TYPE",
                line, ErrorCode.NON_INTEGER_ARRAY_SIZE);
    }
}

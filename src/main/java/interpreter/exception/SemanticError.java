package interpreter.exception;

import interpreter.intermediate.type.DataType;

public class SemanticError extends InterpError {

    public SemanticError(String msg, int line, ErrorCode code) {
        super(msg, line, code);
    }

    @Override
    public String getMessage() {
        return "Semantic error (" + getCode() + ") at line " + this.getLine() + ": " + this.getInnerMsg();
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

    private static String getTypeDesc(DataType type) {
        return "'" + type.getBasicType().toString() + "' " +
                type.getForm().toString();
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

    public static SemanticError newInvalidInitializerError(DataType target, String lex, int line) {
        return new SemanticError(target.getForm().toString() + " '" + lex + "' HAS INVALID INITIALIZER",
                line, ErrorCode.INVALID_INITIALIZER);
    }

    public static SemanticError newInitialIncompatibleTypeError(DataType target, DataType value, int line) {
        return new SemanticError("TYPE " + SemanticError.getTypeDesc(target) +
                "AND TYPE " + SemanticError.getTypeDesc(value) +
                "ARE INCOMPATIBLE WHEN INITIALIZING", line, ErrorCode.INITIAL_INCOMPATIBLE_TYPE);
    }



    public static SemanticError newSymbolUndeclaredError(String lex, int line) {
        return new SemanticError("'" + lex + "' UNDECLARED", line, ErrorCode.SYMBOL_UNDECLARED);
    }

    public static SemanticError newReadWrongTypeError(DataType wrong, int line) {
        return new SemanticError("READ REQUIRED A SCALAR BUT GET A " +
                SemanticError.getTypeDesc(wrong), line, ErrorCode.READ_WRONG_TYPE);
    }

    public static SemanticError newWrongSubscriptedType(DataType wrong, int line) {
        return new SemanticError("WRONG TYPE OF SUBSCRIPTED VALUE " +
                SemanticError.getTypeDesc(wrong), line, ErrorCode.WRONG_SUBSCRIPTED_TYPE);
    }
}

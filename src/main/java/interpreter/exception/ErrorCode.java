package interpreter.exception;

public enum ErrorCode {

    // lex part
    UNEXPECTED_CHAR(1001), IDENTIFIER_NAMING(1002), CONSTANT_DEFINE(1003),

    // parse part
    UNEXPECTED_TOKEN(2001), MISSING_TOKEN(2002),

    // semantic part
    DUP_DECLARATION(3001), INVALID_INITIALIZER(3002), INITIAL_INCOMPATIBLE_TYPE(3003),

    // execution part
    DIV_BY_ZERO(4001), NEG_ARRAY_SIZE(4002), NON_INTEGER_ARRAY_SIZE(4003)

    ;

    private int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}

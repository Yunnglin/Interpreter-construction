package interpreter.exception;

public enum ErrorCode {

    // lex part
    UNEXPECTED_CHAR(1001), IDENTIFIER_NAMING(1002), CONSTANT_DEFINE(1003),

    // parse part
    UNEXPECTED_TOKEN(2001), MISSING_TOKEN(2002),

    // semantic part
    DUP_DECLARATION(3001), INVALID_INITIALIZER(3002), INITIAL_INCOMPATIBLE_TYPE(3003),
    SYMBOL_UNDECLARED(3004), READ_WRONG_TYPE(3005), WRONG_SUBSCRIPTED_TYPE(3006),
    WRONG_NEGATIVE_TYPE(3007), SYMBOL_NOT_CALLABLE(3008), NEG_ARRAY_SIZE(3009),
    NON_INTEGER_ARRAY_SIZE(3010), NON_INTEGER_ARRAY_INDEX(3012),

    // execution part
    DIV_BY_ZERO(4001), BAD_ARRAY_BOUND(4002), UNMATCH_TYPE(4003), WRONG_OPERATE_TYPE(4004),
    MISSING_RETURN(4005), FEW_MANY_ARGS(4006), INCOMPATIBLE_ARG_VAL(4007)

    ;

    private int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}

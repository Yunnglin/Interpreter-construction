package interpreter.exception;

import interpreter.env.Env;
import interpreter.intermediate.type.DataType;

public class ExecutionError extends InterpError {

    public ExecutionError(String msg, int line, ErrorCode code) {
        super(msg, line, code);
    }

    @Override
    public String getMessage() {
        if (this.getLine() > 0) {
            return "Execution error (" + getCode() + ") at line " + this.getLine() + ": " + this.getInnerMsg();
        } else {
            return "Execution error (" + getCode() + "): " + this.getInnerMsg();
        }
    }

    private static String getTypeDesc(DataType type) {
        return "'" + type.getBasicType().toString() + "' " +
                type.getForm().toString();
    }

    private static String getParamPositionStr(int pos) {
        switch (pos) {
            case 1:
                return pos + "st";
            case 2:
                return pos + "nd";
            case 3:
                return pos + "rd";
            default:
                return pos + "th";
        }
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

    public static ExecutionError newDivByZeroError(int line) {
        return new ExecutionError("DIVISION BY ZERO", line, ErrorCode.DIV_BY_ZERO);
    }

    public static ExecutionError newAssignUnmatchTypeError(DataType target, DataType value, int line) {
        return new ExecutionError("TYPE " + ExecutionError.getTypeDesc(target) + "AND TYPE" +
                ExecutionError.getTypeDesc(value) + "ARE INCOMPATIBLE WHEN ASSIGN", line, ErrorCode.UNMATCH_TYPE);
    }

    public static ExecutionError newWhileUnmatchTypeError(DataType value, int line) {
        return new ExecutionError("TYPE  BOOLEAN" + "AND TYPE" +
                ExecutionError.getTypeDesc(value) + "ARE UNMATCHED WHEN EXECUTE", line, ErrorCode.UNMATCH_TYPE);
    }

    public static ExecutionError newWrongOpeTypeError(DataType value, DataType aim, int line) {
        return new ExecutionError("TYPE " + ExecutionError.getTypeDesc(aim) + " AND TYPE" +
                ExecutionError.getTypeDesc(value) + " ARE WORNG OPERATE TYPE", line, ErrorCode.WRONG_OPERATE_TYPE);
    }

    public static ExecutionError newWriteUnmatchTypeError(DataType value, int line) {
        return new ExecutionError("TYPE VOID CAN NOT BE WRITTEN", line, ErrorCode.UNMATCH_TYPE);
    }

    public static ExecutionError newBadArrayBoundError(String identifierName, int line) {
        return new ExecutionError(identifierName + " BAD ARRAY BOUND ", line, ErrorCode.BAD_ARRAY_BOUND);
    }

    public static ExecutionError newMissingReturnError(String lex, int line) {
        return new ExecutionError("MISSING RETURN STATEMENT IN FUNCTION '" + lex + "'",
                line, ErrorCode.MISSING_RETURN);
    }

    public static ExecutionError newFewManyArgsError(String lex, boolean few, int line) {
        return new ExecutionError("TOO " + (few ? "FEW" : "MANY") + " ARGUMENTS FOR FUNCTION '" +
                lex + "'", line, ErrorCode.FEW_MANY_ARGS);
    }

    public static ExecutionError newArgTypeNotCompatible(String lex, DataType argType, DataType valType,
                                                         int argPos, int line) {
        return new ExecutionError("TYPE OF" + getParamPositionStr(argPos) + " ARGUMENT OF '" +
                lex + "' " + getTypeDesc(argType) + " IS INCOMPATIBLE WITH" + getTypeDesc(valType),
                line, ErrorCode.INCOMPATIBLE_ARG_VAL);
    }

    public static ExecutionError newProgEntranceNotFound(String lex) {
        return new ExecutionError("PROGRAM ENTRY '" + lex + "' FUNCTION NOT FOUND",
                -1, ErrorCode.PROG_ENTRANCE_NOT_FOUND);
    }

}

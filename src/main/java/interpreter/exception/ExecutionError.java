package interpreter.exception;

import interpreter.intermediate.type.DataType;

public class ExecutionError extends InterpError {

    public ExecutionError(String msg, int line, ErrorCode code) {
        super(msg, line, code);
    }

    @Override
    public String getMessage() {
        return "Execution error (" + getCode() + ") at line " + this.getLine() + ": " + this.getInnerMsg();
    }

    private static String getTypeDesc(DataType type) {
        return "'" + type.getBasicType().toString() + "' " +
                type.getForm().toString();
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
        return new ExecutionError("TYPE " + ExecutionError.getTypeDesc(aim) + "AND TYPE" +
                ExecutionError.getTypeDesc(value) + "ARE WORNG OPERATE TYPE", line, ErrorCode.WRONG_OPERATE_TYPE);
    }

    public static ExecutionError newWriteUnmatchTypeError(DataType value, int line) {
        return new ExecutionError("TYPE VOID CAN NOT BE WRITTEN", line, ErrorCode.UNMATCH_TYPE);
    }

    public static ExecutionError newBadArrayBoundError(String identifierName, int line) {
        return new ExecutionError(identifierName + " BAD ARRAY BOUND ", line, ErrorCode.BAD_ARRAY_BOUND);
    }

}

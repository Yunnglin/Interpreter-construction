package interpreter.intermediate.type;

public class FuncPrototype {

    private DataType retType;
    private DataType[] paramTypes;

    public FuncPrototype(DataType retType, DataType[] parmTypes) {
        this.retType = retType;
        this.paramTypes = parmTypes;
    }

    public FuncPrototype() {
        this.retType = null;
        this.paramTypes = null;
    }

    public DataType getRetType() {
        return retType;
    }

    public void setRetType(DataType retType) {
        this.retType = retType;
    }

    public DataType[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(DataType[] paramTypes) {
        this.paramTypes = paramTypes;
    }
}

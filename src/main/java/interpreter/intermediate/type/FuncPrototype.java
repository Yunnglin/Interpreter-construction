package interpreter.intermediate.type;

public class FuncPrototype {

    private DataType retType;
    private DataType[] paramTypes;

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj)) {
            return true;
        }

        FuncPrototype prototype = (FuncPrototype) obj;

        // check return type
        if (retType.equals(prototype.retType)) {
            // check amount of params
            if (paramTypes.length != prototype.getParamTypes().length) {
                return false;
            }

            // check each param
            for (int i=0; i<paramTypes.length; ++i) {
                if (!paramTypes[i].equals(prototype.getParamTypes()[i])) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

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

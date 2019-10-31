package interpreter.intermediate.type;

public enum BasicType {
    INT(4), REAL(8), VOID, POINTER(8), FUNC_POINTER(1);

    private int wordSize;

    private BasicType(int wordSize) {
        this.wordSize = wordSize;
    }

    private BasicType() {
        this.wordSize = 0;
    }

    public int getWordSize() {
        return this.wordSize;
    }


    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public static BasicType getBasicType(String type) {
        for (BasicType basicType : BasicType.values()) {
            if (type.equals(basicType.toString())) {
                return basicType;
            }
        }

        return null;
    }
}

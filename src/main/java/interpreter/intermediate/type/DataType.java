package interpreter.intermediate.type;

public class DataType {

    public static class PredefinedType {
        public static DataType TYPE_INT = new DataType(BasicType.INT, TypeForm.SCALAR);
        public static DataType TYPE_REAL = new DataType(BasicType.REAL, TypeForm.SCALAR);
        public static DataType TYPE_VOID = new DataType(BasicType.VOID, TypeForm.SCALAR);
    }

    private TypeForm form;
    private BasicType basicType;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        DataType type = (DataType) obj;
        if (this.form.equals(type.getForm()) &&
                this.basicType.equals(type.getBasicType())) {
            return true;
        }

        return false;
    }

    public DataType(BasicType basicType, TypeForm form) {
        this.form = form;
        this.basicType = basicType;
    }

    public BasicType getBasicType() {
        return basicType;
    }

    public TypeForm getForm() {
        return form;
    }

}

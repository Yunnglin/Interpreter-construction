package interpreter.env;

import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.BasicType;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.TypeForm;

import java.util.ArrayList;

public class TypeSystem {
    /**
     * check the whether the assignment is compatible
     * @param target the type of assignment target
     * @param value the type of assignment value
     * @return whether the assignment compatible
     */
    public boolean assignCompatible(DataType target, DataType value) {
        // if void exists, absolutely incompatible
        if (target.getBasicType().equals(BasicType.VOID)
                || value.getBasicType().equals(BasicType.VOID)) {
            return false;
        }

        if ((target.equals(value)) && (target.getForm().equals(TypeForm.SCALAR))) {
            // the same type in scalar form is compatible
            return true;
        } else if ((target.equals(DataType.PredefinedType.TYPE_REAL))
                && (value.equals(DataType.PredefinedType.TYPE_INT))) {
            // int(4) can be cast to real(8) implicitly
            return true;
        }

        return false;
    }

    /**
     * check whether the return value is compatible to prototype
     * @param proto the type of return value defined in prototype
     * @param value the type of return value in execution
     * @return
     */
    public boolean returnCompatible(DataType proto, DataType value) {
        if (proto.equals(value)) {
            // the same type as which defined in function prototype
            return true;
        } else {
            // otherwise, check whether the two types can do assignment
            return assignCompatible(proto, value);
        }
    }

    /**
     * check whether the value can be written
     * @param test the type of value to write
     * @return whether the type is legal
     */
    public boolean writeCompatible(DataType test) {
        if (test.getBasicType().equals(BasicType.VOID))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * check whether the value can be placed in condition
     * @param test the type of the value that to be condition
     * @return whether the value can be condition
     */
    public boolean whileCompatible(DataType test) {
        if (test.getForm().equals(TypeForm.SCALAR)&&!test.getBasicType().equals(BasicType.VOID))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * check whether two values can do comparison
     * @param type1 type of value1
     * @param type2 type of value2
     * @return whether value1 and value1 can compare
     */
    public boolean compareCompatible(DataType type1, DataType type2) {
        if (type1.equals(DataType.PredefinedType.TYPE_VOID)
                || type2.equals(DataType.PredefinedType.TYPE_VOID)) {
            return false;
        }

        if (type1.getForm().equals(TypeForm.SCALAR) && type2.getForm().equals(TypeForm.SCALAR)) {
            // one integer and one real number
            if ((type1 == type2)
                    || (type1.equals(DataType.PredefinedType.TYPE_INT) &&
                    type2.equals(DataType.PredefinedType.TYPE_REAL))
                    || (type1.equals(DataType.PredefinedType.TYPE_REAL) &&
                    type2.equals(DataType.PredefinedType.TYPE_INT))) {
                return true;
            }
        }

        return false;
    }

    /**
     * check whether the initialization is compatible according to the type declared
     * @param target the type of declared symbol
     * @param value the type of initial value
     * @return whether value can used to initialize target
     */
    public boolean initializeCompatible(DataType target, DataType value) {
        boolean compatible = assignCompatible(target, value);
        if (compatible) {
            // can assign to target
            return true;
        } else if (target.getForm().equals(TypeForm.ARRAY)
                && value.getForm().equals(TypeForm.ARRAY)) {
            // array initialization
            DataType targetScalar = new DataType(target.getBasicType(), TypeForm.SCALAR);
            DataType valueScalar = new DataType(value.getBasicType(), TypeForm.SCALAR);

            return assignCompatible(targetScalar, valueScalar);
        }

        return false;
    }

    /**
     * get the basic data type according to the string of basic type
     * @param type the string of basic type
     * @return the corresponding data type
     */
    public DataType getBasicDataType(String type) {
        BasicType basicType = null;
        if ( (basicType = BasicType.getBasicType(type)) != null) {
            return new DataType(basicType, TypeForm.SCALAR);
        }

        return null;
    }

    /**
     * get the basic data type according to a basic type
     * @param basicType the basic type
     * @return the corresponding data type
     */
    public DataType getBasicDataType(BasicType basicType) {
        return new DataType(basicType, TypeForm.SCALAR);
    }

    /**
     * return the default value for a specific basic type
     * @param basicType the basic type
     * @return the default value for the basic type
     */
    private Object defaultBasicInitializer(BasicType basicType) {
        if (basicType.equals(BasicType.INT)) {
            // integer, 0
            return 0;
        } else if (basicType.equals(BasicType.REAL)) {
            // real, 0.0
            return 0.0;
        } else if (basicType.equals(BasicType.VOID)) {
            // void, null
            return null;
        } else if (basicType.equals(BasicType.FUNC_POINTER)) {
            // TODO function pointer
        } else if (basicType.equals(BasicType.POINTER)) {
            // TODO pointer
        }

        return null;
    }

    /**
     * set the value to be default value according to the type
     * @param entry the entry of the symbol to initialize
     */
    public void defaultInitializer(SymTblEntry entry) {
        // get the type
        DataType type = (DataType) entry.getValue(SymTbl.SymTblKey.TYPE);

        if (type.getForm().equals(TypeForm.SCALAR)) {
            // single value of basic type
            entry.addValue(SymTbl.SymTblKey.VALUE, defaultBasicInitializer(type.getBasicType()));

        } else if (type.getForm().equals(TypeForm.ARRAY)) {
            // array initializing
            Integer size = (Integer) entry.getValue(SymTbl.SymTblKey.ARRAY_SIZE);
            ArrayList<Object> values = new ArrayList<>();
            for (int i=0; i<size; ++i) {
                values.add(defaultBasicInitializer(type.getBasicType()));
            }
            entry.addValue(SymTbl.SymTblKey.VALUE, values.toArray());

        }

    }
}

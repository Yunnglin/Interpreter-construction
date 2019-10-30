package interpreter.intermediate;

import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.BasicType;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.TypeForm;

import java.util.Stack;

public class Env {

    private Stack<SymTbl> symTblStack;
    private int curNestingLevel;

    public Env() {
        symTblStack = new Stack<SymTbl>();
        curNestingLevel = 0;
        symTblStack.push(new SymTbl(curNestingLevel));
    }

    /**
     * push a new symbol table into the stack of symbol tables
     *
     * @param table
     */
    public void pushSymTbl(SymTbl table) {
        symTblStack.push(table);
        curNestingLevel++;
        table.setNestingLevel(curNestingLevel);
    }

    /**
     * pop the top table out of the stack of symbol tables
     *
     * @return the popped item
     */
    public SymTbl popSymTbl() {
        curNestingLevel--;
        return symTblStack.pop();
    }

    /**
     * find a symbol table entry in available scope
     *
     * @param name the name of the entry
     * @return the entry found or null if the entry is not in all tables
     */
    public SymTblEntry findSymTblEntry(String name) {
        SymTblEntry entry;
        for (int i = curNestingLevel; i >= 0; i--) {
            if ((entry = symTblStack.get(i).find(name)) != null) {
                return entry;
            }
        }

        return null;
    }

    public SymTbl getCurScopeSymTbl() {
        return this.symTblStack.get(curNestingLevel);
    }

    public boolean assignCompatible(DataType target, DataType value) {
        if (target.getBasicType().equals(BasicType.VOID)
            || value.getBasicType().equals(BasicType.VOID)) {
            return false;
        }

        if ((target.equals(value)) && (target.getForm().equals(TypeForm.SCALAR))) {
            return true;
        } else if ((target.equals(DataType.PredefinedType.TYPE_REAL))
        && (value.equals(DataType.PredefinedType.TYPE_INT))) {
            return true;
        }

        return false;
    }

    public boolean compareCompatible(DataType type1, DataType type2) {
        if (type1.equals(DataType.PredefinedType.TYPE_VOID)
                || type2.equals(DataType.PredefinedType.TYPE_VOID)) {
            return false;
        }

        if (type1.getForm().equals(TypeForm.SCALAR) && type2.getForm().equals(TypeForm.SCALAR)) {
            // one integer and one real number
            if ( (type1 == type2)
                    || (type1.equals(DataType.PredefinedType.TYPE_INT) &&
                            type2.equals(DataType.PredefinedType.TYPE_REAL))
                    || (type1.equals(DataType.PredefinedType.TYPE_REAL) &&
                            type2.equals(DataType.PredefinedType.TYPE_INT))) {
                return true;
            }
        }

        return false;
    }

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

//    public DataType str2DataType(String type) {
//        BasicType basicType = null;
//        if ( (basicType = BasicType.getBasicType(type)) != null) {
//            return new DataType(basicType, TypeForm.SCALAR);
//        }
//
//        return null;
//    }

}

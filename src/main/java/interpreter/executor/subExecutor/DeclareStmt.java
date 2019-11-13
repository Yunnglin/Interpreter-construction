package interpreter.executor.subExecutor;

import interpreter.exception.SemanticError;
import interpreter.executor.BaseExecutor;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.node.INode.INodeKey;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTbl.SymTblKey;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.BasicType;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.TypeForm;
import interpreter.utils.INodeUtils;

import java.util.ArrayList;

public class DeclareStmt extends BaseExecutor {

    public DeclareStmt(Env env) {
        super(env);
    }

    /**
     * Execute declare statement node
     * do declaration and initialization
     * @param root INode, the node for a declare statement
     * @return null, no result returned
     * @throws Exception
     * @throws ReturnStmt.ReturnSignal
     */
    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.DECLARE_STMT)) {
            throw new Exception("parse error in declare stmt at line " +
                    root.getAttribute(INodeKey.LINE));
        }
        INode type = root.getChild(0);
        INode declaratorList = root.getChild(1);

        String typeName = (String) type.getChild(0).getAttribute(INodeKey.NAME);
        ArrayList<INode> declarators = INodeUtils.getLeftMostNodes(declaratorList);

        for (INode declarator : declarators) {
            declare(typeName, declarator);
        }

        return null;
    }

    /**
     * do declaration, declare a symbol in current scope
     * @param type the type string of the symbol
     * @param declarator INode, the declarator node
     * @throws Exception
     * @throws ReturnStmt.ReturnSignal
     */
    private void declare(String type, INode declarator) throws Exception, ReturnStmt.ReturnSignal {
        SymTbl symTbl = env.getCurScopeSymTbl();

        // get the nodes
        ArrayList<INode> children = declarator.getChildren();
        INode identifier = children.get(0);
        String idName = (String) identifier.getAttribute(INodeKey.NAME);
        SymTblEntry entry = symTbl.find(idName);
        BasicType basicType = BasicType.getBasicType(type);

        // check the data type cannot be void
        // if pointer added, should check whether any pointer exists
        if (env.getTypeSystem().getBasicDataType(type).equals(DataType.PredefinedType.TYPE_VOID)) {
            throw SemanticError.newDeclareVoidError((String) identifier.getAttribute(INodeKey.NAME),
                    (Integer) identifier.getAttribute(INodeKey.LINE));
        }

        if (entry != null) {
            // a duplicate declaration
            throw SemanticError.newDupDeclareError(idName, (int) declarator.getAttribute(INodeKey.LINE),
                    (int) entry.getValue(SymTblKey.LINE));
        } else {
            int childrenSize = children.size();
            SymTblEntry newEntry = new SymTblEntry(idName);
            // set line to symbol table entry
            newEntry.addValue(SymTblKey.LINE, declarator.getAttribute(INodeKey.LINE));

            if (childrenSize == 1) {
                // only a identifier
                // set it to a scalar of specific type
                newEntry.addValue(SymTblKey.TYPE, new DataType(basicType, TypeForm.SCALAR));
                // set initial value
                env.getTypeSystem().defaultInitializer(newEntry);
                symTbl.addEntry(newEntry);
            } else {
                // find whether there is a initializer
                INode initializer = null;
                for (INode child : children) {
                    if (child.getSymbol().equals(LALRNonterminalSymbol.INITIALIZER)) {
                        initializer = child;
                    }
                }

                if (childrenSize == 2) {
                    // identifier and its initializer
                    newEntry.addValue(SymTblKey.TYPE, new DataType(basicType, TypeForm.SCALAR));
                } else {
                    // an array
                    newEntry.addValue(SymTblKey.TYPE, new DataType(basicType, TypeForm.ARRAY));
                    if (children.get(2).getSymbol().equals(LALRNonterminalSymbol.EXPR)) {
                        // a fixed array
                        // get the value of expr
                        INode indexExpr = children.get(2);
                        Object[] values = (Object[]) executeNode(indexExpr);
                        DataType lenType = (DataType) values[0];

                        // check type to be integer
                        if (!lenType.equals(DataType.PredefinedType.TYPE_INT)) {
                            throw SemanticError.newNonIntegerArraySizeError(idName,
                                    (Integer) identifier.getAttribute(INodeKey.LINE));
                        }
                        Integer arrSize = (Integer) values[1];
                        // check type to be non-negative
                        if (arrSize < 0) {
                            throw SemanticError.newNegativeArraySizeError(idName,
                                    (Integer) identifier.getAttribute(INodeKey.LINE));
                        }
                        // set array size attribute
                        newEntry.addValue(SymTblKey.ARRAY_SIZE, values[1]);
                    }
                }
                // initialize values
                initialize(newEntry, initializer);
            }
            // add to symbol table
            symTbl.addEntry(newEntry);
        }
    }

    /**
     * do initialization, set initial value to the corresponding entry
     * @param entry SymTblEntry, the entry for the symbol
     * @param initializer INode, the initializer node
     * @throws Exception
     * @throws ReturnStmt.ReturnSignal
     */
    private void initialize(SymTblEntry entry, INode initializer) throws Exception, ReturnStmt.ReturnSignal {
        if (initializer != null) {
            DataType entryType = (DataType) entry.getValue(SymTblKey.TYPE);
            ArrayList<INode> children = initializer.getChildren();
            // set initial value to new symbol table entry
            if (children.get(1).getSymbol().equals(LALRNonterminalSymbol.EXPR)) {
                // a scalar value
                // get the value of expr
                INode exprNode = children.get(1);
                Object[] exprValues = (Object[]) executeNode(exprNode);
                DataType exprType = (DataType) exprValues[0];
                Object exprValue = exprValues[1];

                // check type
                if (!entryType.getForm().equals(TypeForm.SCALAR)) {
                    // invalid initializer
                    throw SemanticError.newInvalidInitializerError(entryType, entry.getName(),
                            (Integer) initializer.getAttribute(INodeKey.LINE));
                }
                if (!env.getTypeSystem().initializeCompatible(entryType, exprType)) {
                    // incompatible type
                    throw SemanticError.newInitialIncompatibleTypeError(entryType, exprType,
                            (Integer) initializer.getAttribute(INodeKey.LINE));
                }

                //  set the value column
                entry.addValue(SymTblKey.VALUE, exprValue);
            } else {
                INode initList = children.get(2);
                ArrayList<INode> exprArr = INodeUtils.getLeftMostNodes(initList);

                if (!entryType.getForm().equals(TypeForm.ARRAY)) {
                    // invalid initializer
                    throw SemanticError.newInvalidInitializerError(entryType, entry.getName(),
                            (Integer) initializer.getAttribute(INodeKey.LINE));
                }

                // initialize array
                DataType eleType = new DataType(entryType.getBasicType(), TypeForm.SCALAR);
                ArrayList<Object> values = new ArrayList<>();
                for (INode expr : exprArr) {
                    Object[] exprValues = (Object[]) executeNode(expr);
                    DataType exprType = (DataType) exprValues[0];
                    Object exprValue = exprValues[1];
                    if (!env.getTypeSystem().initializeCompatible(eleType, exprType)) {
                        // the element type is not compatible
                        throw SemanticError.newInitialIncompatibleTypeError(eleType, exprType,
                                (Integer) expr.getAttribute(INodeKey.LINE));
                    }
                    values.add(exprValue);
                }

                // check size the declarator is size-fixed
                Object size = entry.getValue(SymTblKey.ARRAY_SIZE);
                if (size != null) {
                    if ((Integer) size < values.size()) {
                        // the size of initial values greater
                        // TODO WARNING ?
                    }

                    Object[] array = new Object[(Integer) size];
                    for (int i=0; i<values.size() && i<array.length; ++i) {
                        array[i] = values.get(i);
                    }
                    entry.addValue(SymTblKey.VALUE, array);
                } else {
                    entry.addValue(SymTblKey.ARRAY_SIZE, values.size());
                    entry.addValue(SymTblKey.VALUE, values.toArray());
                }
            }
        } else {
            // default initializer
            env.getTypeSystem().defaultInitializer(entry);
        }
    }

}

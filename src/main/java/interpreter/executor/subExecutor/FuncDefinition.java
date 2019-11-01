package interpreter.executor.subExecutor;

import interpreter.exception.SemanticError;
import interpreter.executor.BaseExecutor;
import interpreter.env.Env;
import interpreter.grammar.TokenTag;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.BasicType;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.FuncPrototype;
import interpreter.intermediate.type.TypeForm;
import interpreter.utils.INodeUtils;

import java.util.ArrayList;

public class FuncDefinition extends BaseExecutor {

    public FuncDefinition(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.FUNC_DEFINITION)) {
            throw new Exception("parse error in function definition at line " +
                    root.getAttribute(INode.INodeKey.LINE));
        }

        // type func-declarator { stmt-list }
        INode type = root.getChild(0).getChild(0);
        INode declarator = root.getChild(1);
        INode stmts = root.getChild(3);
        String typeName = (String) type.getAttribute(INode.INodeKey.NAME);

        // declare the function
        SymTblEntry entry = funcDeclare(typeName, declarator);
        ArrayList<INode> body = stmts.getSymbol().equals(LALRNonterminalSymbol.STMT_LIST)
                ? INodeUtils.getLeftMostNodes(stmts)
                : new ArrayList<>();
        int stmtLen = body.size();

        // set function body
        entry.addValue(SymTbl.SymTblKey.FUNC_BODY, body.toArray(new INode[stmtLen]));

        if (entry.getName().equals(Env.defaultMainEntranceName)) {
            env.setMainEntry(entry);
        }

        return null;
    }

    private SymTblEntry funcDeclare(String type, INode declarator) throws Exception, ReturnStmt.ReturnSignal {
        // func-declarator -> identifier() | identifier(param-list)
        INode identifier = declarator.getChild(0);
        INode more = declarator.getChild(2);
        String idName = (String) identifier.getAttribute(INode.INodeKey.NAME);
        SymTbl curScopeSymTbl = env.getCurScopeSymTbl();
        SymTblEntry entry = curScopeSymTbl.find(idName);
        // construct prototype
        DataType retType = env.getBasicDataType(type);
        FuncPrototype prototype = new FuncPrototype();
        prototype.setRetType(retType);

        if (entry != null) {
            // a duplicate function definition
            throw SemanticError.newDupDeclareError(idName,
                    (Integer) declarator.getAttribute(INode.INodeKey.LINE),
                    (Integer) entry.getValue(SymTbl.SymTblKey.LINE));
        }

        SymTblEntry newEntry = new SymTblEntry(idName);
        // set type
        newEntry.addValue(SymTbl.SymTblKey.TYPE, DataType.PredefinedType.TYPE_FUNC);
        // set prototype
        newEntry.addValue(SymTbl.SymTblKey.FUNC_PROTOTYPE, prototype);
        // set line to entry
        newEntry.addValue(SymTbl.SymTblKey.LINE, declarator.getAttribute(INode.INodeKey.LINE));
        // initialize symbol table of function
        SymTbl symTbl = new SymTbl();
        newEntry.addValue(SymTbl.SymTblKey.SYMTBL, symTbl);

        if (more.getSymbol().equals(TokenTag.R_PARENTHESES)) {
            // terminated, no param
            prototype.setParamTypes(new DataType[0]);
        } else {
            // has params
            ArrayList<INode> paramDeclarations = INodeUtils.getLeftMostNodes(more);
            ArrayList<DataType> paramTypes = new ArrayList<>();
            int paramLen = paramTypes.size();

            for (INode paramDecl : paramDeclarations) {
                paramTypes.add(paramDeclare(symTbl, paramDecl));
            }
            prototype.setParamTypes((DataType[]) paramTypes.toArray(new DataType[paramLen]));
        }

        // declare function in symbol table
        curScopeSymTbl.addEntry(newEntry);

        return newEntry;
    }

    private DataType paramDeclare(SymTbl initialTbl, INode paramDeclaration) throws Exception, ReturnStmt.ReturnSignal {
        INode type = paramDeclaration.getChild(0);
        INode identifier = paramDeclaration.getChild(1);
        SymTblEntry newEntry = new SymTblEntry((String) identifier.getAttribute(INode.INodeKey.NAME));
        String typeName = (String) type.getChild(0).getAttribute(INode.INodeKey.NAME);
        DataType paramType;

        if (paramDeclaration.getChildren().size() > 2) {
            // type id [expr]
            paramType = new DataType(BasicType.getBasicType(typeName), TypeForm.ARRAY);
            INode exprNode = paramDeclaration.getChild(3);
            Object[] values = (Object[]) executeNode(exprNode);
            DataType lenType = (DataType) values[0];

            // check type to be integer
            if (!lenType.equals(DataType.PredefinedType.TYPE_INT)) {
                throw SemanticError.newNonIntegerArraySizeError((String) identifier.getAttribute(INode.INodeKey.NAME),
                        (Integer) identifier.getAttribute(INode.INodeKey.LINE));
            }
            Integer arrSize = (Integer) values[1];
            // check type to be non-negative
            if (arrSize < 0) {
                throw SemanticError.newNegativeArraySizeError((String) identifier.getAttribute(INode.INodeKey.NAME),
                        (Integer) identifier.getAttribute(INode.INodeKey.LINE));
            }
            // set array size attribute
            newEntry.addValue(SymTbl.SymTblKey.ARRAY_SIZE, values[1]);
        } else {
            // type id
            paramType = env.getBasicDataType(typeName);
        }
        newEntry.addValue(SymTbl.SymTblKey.TYPE, paramType);
        // set param initial value
        env.defaultInitializer(newEntry);
        // add to symbol table
        initialTbl.addEntry(newEntry);

        return paramType;
    }

}

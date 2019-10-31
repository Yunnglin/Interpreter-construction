package interpreter.executor.subExecutor;

import interpreter.exception.SemanticError;
import interpreter.executor.BaseExecutor;
import interpreter.env.Env;
import interpreter.grammar.TokenTag;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.DataType;
import interpreter.utils.List2Array;

import java.util.ArrayList;

public class FuncDefinition extends BaseExecutor {

    public FuncDefinition(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        if (root.getSymbol().equals(LALRNonterminalSymbol.FUNC_DEFINITION)) {
            throw new Exception("parse error in function definition at line " +
                    root.getAttribute(INode.INodeKey.LINE));
        }

        // type func-declarator { stmt-list }
        INode type = root.getChild(0);
        INode declarator = root.getChild(1);
        INode stmts = root.getChild(3);
        String typeName = (String) type.getAttribute(INode.INodeKey.NAME);

        // declare the function
        funcDeclare(typeName, declarator);

        ArrayList<INode> procedure = List2Array.getArray(stmts);

        return null;
    }

    private void funcDeclare(String type, INode declarator) throws SemanticError {
        // func-declarator -> identifier() | identifier(param-list)
        INode identifier = declarator.getChild(0);
        INode more = declarator.getChild(2);
        String idName = (String) identifier.getAttribute(INode.INodeKey.NAME);
        SymTbl curScopeSymTbl = env.getCurScopeSymTbl();
        SymTblEntry entry = curScopeSymTbl.find(idName);

        if (entry != null) {
            // a duplicate function definition
            throw SemanticError.newDupFuncDefinitionError(idName,
                    (Integer) declarator.getAttribute(INode.INodeKey.LINE),
                    (Integer) entry.getValue(SymTbl.SymTblKey.LINE));
        }

        SymTblEntry newEntry = new SymTblEntry(idName);
        // set line to entry
        newEntry.addValue(SymTbl.SymTblKey.LINE, declarator.getAttribute(INode.INodeKey.LINE));
        // initialize symbol table of function
        SymTbl symTbl = new SymTbl();
        newEntry.addValue(SymTbl.SymTblKey.SYMTBL, symTbl);

        if (more.getSymbol().equals(TokenTag.R_PARENTHESES)) {
            // terminated, no param

        } else {
            // has params
            ArrayList<INode> paramDeclarations = List2Array.getArray(more);

            for (INode paramDecl : paramDeclarations) {
            }
        }
    }

//    private DataType paramDeclare(SymTbl initialTbl, INode paramDeclarator) {
//
//    }

}

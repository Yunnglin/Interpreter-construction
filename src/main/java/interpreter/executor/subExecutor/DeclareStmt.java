package interpreter.executor.subExecutor;

import interpreter.exception.SemanticError;
import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.Env;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.node.INode.INodeKey;
import interpreter.grammar.TokenTag;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTbl.SymTblKey;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.TypeForm;
import interpreter.utils.List2Array;

import java.util.ArrayList;

public class DeclareStmt extends BaseExecutor {

    public DeclareStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.DECLARE_STMT)) {
            System.out.println("parse error in declare stmt");
            return null;
        }
        INode type = root.getChild(0);
        INode declaratorList = root.getChild(1);

        String typeName = (String) type.getChild(0).getAttribute(INodeKey.NAME);
        ArrayList<INode> declarators = List2Array.getArray(declaratorList);

        for (INode declarator : declarators) {
            declare(typeName, declarator);
        }

        return null;
    }

    private void declare(String type, INode declarator) throws Exception {
        SymTbl symTbl = env.getCurScopeSymTbl();
        ExecutorFactory factory = ExecutorFactory.getExecutorFactory();

        // get the nodes
        ArrayList<INode> children = declarator.getChildren();
        INode identifier = children.get(0);
        String idName = (String) identifier.getAttribute(INodeKey.NAME);
        SymTblEntry entry = symTbl.find(idName);

        if (entry != null) {
            // a duplicate declaration
            throw SemanticError.newDupDeclareError(idName, (int) identifier.getAttribute(INodeKey.LINE),
                    (int) entry.getValue(SymTblKey.LINE));
        } else {
            int childrenSize = children.size();
            SymTblEntry newEntry = new SymTblEntry(idName);
            // set type to symbol table entry
            newEntry.addValue(SymTblKey.TYPE, type);

            if (childrenSize == 1) {
                // only a identifier
                // set it to a scalar of specific type
                newEntry.addValue(SymTblKey.TYPE, type);
                newEntry.addValue(SymTblKey.FORM, TypeForm.SCALAR);
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
                    newEntry.addValue(SymTblKey.FORM, TypeForm.SCALAR);
                } else {
                    // an array
                    newEntry.addValue(SymTblKey.FORM, TypeForm.ARRAY);
                    if (children.get(2).getSymbol().equals(LALRNonterminalSymbol.EXPR)) {
                        // a fixed array
                        // get the value of expr
                        INode indexExpr = children.get(2);
                        Executor exprExecutor = factory.getExecutor(indexExpr, env);
                        Object value = exprExecutor.Execute(indexExpr);
                        // TODO check type to be integer

                        newEntry.addValue(SymTblKey.ELEMENT_NUMBER, value);
                    }
                }
                initialize(newEntry, initializer);
            }
        }
    }

    private void initialize(SymTblEntry entry, INode initializer) throws Exception {
        ExecutorFactory factory = ExecutorFactory.getExecutorFactory();

        if (initializer != null) {
            ArrayList<INode> children = initializer.getChildren();
            // set initial value to new symbol table entry
            if (children.get(1).getSymbol().equals(LALRNonterminalSymbol.EXPR)) {
                // a scalar value
                // get the value of expr
                INode exprNode = children.get(1);
                Executor exprExecutor = factory.getExecutor(exprNode, env);
                Object exprValue = exprExecutor.Execute(exprNode);

                // TODO semantic error
                if (entry.getValue(SymTblKey.FORM) != TypeForm.SCALAR) {
                    // invalid initializer
                }
                //if (entry.getValue(SymTblKey.TYPE) != type) {
                //    // mismatched type
                //}

                // TODO set the value column
                // entry.addValue(SymTblKey.VALUE, exprValue);
            } else {
                INode initList = children.get(2);
                ArrayList<INode> exprArr = List2Array.getArray(initList);

                for (INode expr : exprArr) {
                    Executor exprExecutor = factory.getExecutor(expr, env);
                    Object exprValue = exprExecutor.Execute(expr);
                }

                // TODO semantic error
                if (entry.getValue(SymTblKey.FORM) != TypeForm.ARRAY) {
                    // invalid initializer
                }
                // mismatch type
                // invalid initializer due to wrong size array initial value
            }
        }
    }

}

package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTbl;
import interpreter.utils.List2Array;

import java.util.ArrayList;

public class CompoundStmt extends BaseExecutor {
    public CompoundStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.COMPOUND_STMT)) {
            throw new Exception("parse error in compound stmt");
        }
        int childSize = root.getChildren().size();
        if (childSize == 2) {//{}
            return null;
        }
        if (childSize == 3) {//{stmt_list}

            INode stmtList = root.getChild(1);

            env.pushSymTbl(new SymTbl());//push a deeper sym table
            ArrayList<INode> stmts = List2Array.getArray(stmtList);
            try {
                for (INode stmt : stmts) {
                    executeNode(stmt);
                }
            } catch (ReturnStmt.ReturnSignal returnSignal) {
                throw returnSignal;
            } finally {
                env.popSymTbl(); //pop current sym table
            }

        }
        return null;
    }
}

package interpreter.executor.subExecutor;

import interpreter.exception.ExecutionError;
import interpreter.executor.BaseExecutor;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.type.BasicType;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.TypeForm;

public class IfStmt extends BaseExecutor {
    public IfStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.IF_STMT)) {
            throw new Exception("parse error in if stmt");
        }
        // if ( expr ) compound_stmt more_ifelse
        INode expr = root.getChild(2);

        Object[] exprRes = (Object[]) executeNode(expr);
        DataType type = (DataType) exprRes[0];
        if ((!type.getForm().equals(TypeForm.SCALAR)) || (type.getBasicType().equals(BasicType.VOID))) {
            throw ExecutionError.newWhileUnmatchTypeError(type,
                    (Integer) expr.getAttribute(INode.INodeKey.LINE));
        }

        boolean result = false;
        if (type.getBasicType().equals(BasicType.INT)) {
            if ((int) exprRes[1] == 1)
                result = true;
        } else {
            if ((double) exprRes[1] != 0)
                result = true;
        }

        if (result) {
            INode compoundStmt = root.getChild(4);
            return executeNode(compoundStmt);
        } else {
            INode moreIf = root.getChild(5);
            return ifElse(moreIf);
        }
    }

    private Object ifElse(INode moreIf) throws Exception, ReturnStmt.ReturnSignal {
        int childSize = moreIf.getChildren().size();
        if (childSize == 0) {
            return null;
        }
        if (childSize == 2) {
            INode elseStmt = moreIf.getChild(1);
            return executeNode(elseStmt.getChild(0));
        }
        return null;
    }
}

package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;

public class IfStmt extends BaseExecutor {
    public IfStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.IF_STMT)) {
            System.out.println("parse error in if stmt");
            return null;
        }
        // if ( expr ) compound_stmt more_ifelse
        INode expr = root.getChild(2);
        //TODO use the return of expr
        int result = (int)executeNode(expr);
        if(result==1){
            INode compoundStmt = root.getChild(4);
            return executeNode(compoundStmt);
        }else if(result==0){
            INode moreIf = root.getChild(5);
            return ifElse(moreIf);
        }
        return null;
    }

    private Object ifElse(INode moreIf) throws Exception {
        int childSize = moreIf.getChildren().size();
        if(childSize == 0){
            return null;
        }
        if(childSize == 2){
            INode elseStmt = moreIf.getChild(1);
            return executeNode(elseStmt.getChild(0));
        }
        return null;
    }
}

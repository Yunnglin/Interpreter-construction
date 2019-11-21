package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.executor.signal.ForceExitSIgnal;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;

public class E extends BaseExecutor {
    public E(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception, ForceExitSIgnal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.E)) {
            throw new Exception("parse error in E");
        }
        INode prog = root.getChild(0);
        try {
            progress(prog);
        } catch (ReturnStmt.ReturnSignal returnSignal) {
            throw new Exception("unknown error, the return value not catched");
        }
        return null;
    }

    private void progress(INode prog) throws Exception, ReturnStmt.ReturnSignal, ForceExitSIgnal {
        int childSize = prog.getChildren().size();

        INode externDeclaration = prog.getChild(0);
        executeNode(externDeclaration);
        //[prog]
        if(childSize==2){
            progress(prog.getChild(1));
        }
    }
}

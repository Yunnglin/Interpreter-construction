package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;

public class E extends BaseExecutor {
    public E(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
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

    private void progress(INode prog) throws Exception, ReturnStmt.ReturnSignal {
        int childSize = prog.getChildren().size();
        ExecutorFactory factory = ExecutorFactory.getExecutorFactory();

        INode externDeclaration = prog.getChild(0);
        Executor executor = factory.getExecutor(externDeclaration,env);
        executor.Execute(externDeclaration);
        //[prog]
        if(childSize==2){
            progress(prog.getChild(1));
        }
    }
}

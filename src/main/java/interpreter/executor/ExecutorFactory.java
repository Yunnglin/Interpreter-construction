package interpreter.executor;
import interpreter.executor.subExecutor.*;
import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;
public class ExecutorFactory
{
    private static ExecutorFactory executorFactory=null;
    private ExecutorFactory()
    {

    }
    public static ExecutorFactory getExecutorFactory()
    {
        if(executorFactory==null)
        {
            executorFactory=new ExecutorFactory();
        }
        return executorFactory;
    }
    public Executor getExecutor(INode node, Env env)
    {
        //获取节点类型
        GrammarSymbol type=node.getSymbol();
        if (type==LALRNonterminalSymbol.EXPR)
        {
            return new Expr(env);
        }else if(type == LALRNonterminalSymbol.E){
            return new E(env);
        }else if(type == LALRNonterminalSymbol.EXTERN_DECLARATION){
            return new ExtrenDeclaration(env);
        }else if(type == LALRNonterminalSymbol.STMT){
            return new Stmt(env);
        }else if(type ==LALRNonterminalSymbol.COMPOUND_STMT){
            return new CompoundStmt(env);
        }else if(type == LALRNonterminalSymbol.IF_STMT){
            return new IfStmt(env);
        }else if(type == LALRNonterminalSymbol.DECLARE_STMT) {
            return new DeclareStmt(env);
        }else if(type == LALRNonterminalSymbol.EXPR_STMT) {
            return new ExprStmt(env);
        }

        return null;

    }
}

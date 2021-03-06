package interpreter.executor;

import interpreter.executor.subExecutor.*;
import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;

public class ExecutorFactory {
    private static ExecutorFactory executorFactory = null;

    private ExecutorFactory() {

    }

    public static ExecutorFactory getExecutorFactory() {
        if (executorFactory == null) {
            executorFactory = new ExecutorFactory();
        }
        return executorFactory;
    }

    public Executor getExecutor(INode node, Env env) {
        //获取节点类型
        GrammarSymbol type = node.getSymbol();
        if (type == LALRNonterminalSymbol.EXPR) {
            return new Expr(env);
        } else if (type == LALRNonterminalSymbol.E) {
            return new E(env);
        } else if (type == LALRNonterminalSymbol.EXTERN_DECLARATION) {
            return new ExtrenDeclaration(env);
        } else if (type == LALRNonterminalSymbol.STMT) {
            return new Stmt(env);
        } else if (type == LALRNonterminalSymbol.COMPOUND_STMT) {
            return new CompoundStmt(env);
        } else if (type == LALRNonterminalSymbol.IF_STMT) {
            return new IfStmt(env);
        } else if (type == LALRNonterminalSymbol.DECLARE_STMT) {
            return new DeclareStmt(env);
        } else if (type == LALRNonterminalSymbol.EXPR_STMT) {
            return new ExprStmt(env);
        } else if (type == LALRNonterminalSymbol.ASSIGN_STMT) {
            return new AssignStmt(env);
        } else if (type == LALRNonterminalSymbol.FUNC_DEFINITION) {
            return new FuncDefinition(env);
        } else if (type == LALRNonterminalSymbol.RETURN_STMT) {
            return new ReturnStmt(env);
        } else if (type == LALRNonterminalSymbol.READ_STMT) {
            return new ReadStmt(env);
        } else if (type == LALRNonterminalSymbol.WHILE_STMT) {
            return new WhileStmt(env);
        } else if (type == LALRNonterminalSymbol.WRITE_STMT) {
            return new WriteStmt(env);
        }

        return null;

    }
}

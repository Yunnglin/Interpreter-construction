package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.TokenTag;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.Env;
import interpreter.intermediate.node.INode;

public class Expr extends BaseExecutor {

    public Expr(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.EXPR)) {
            System.out.println("parse error in expression");
            return null;
        }

        INode relExpr = root.getChild(0);

        return calExpr(relExpr);
    }

    private Object calExpr(INode expr) throws Exception {
        GrammarSymbol nodeSymbol = expr.getSymbol();

        if (nodeSymbol.equals(LALRNonterminalSymbol.RELATIONAL_EXPR)
        || nodeSymbol.equals(LALRNonterminalSymbol.SIMPLE_EXPR)
        || nodeSymbol.equals(LALRNonterminalSymbol.TERM)) {
            // relational, add / subtract, multiplication or division expression calculation.
            // left-associative operation.
            INode leftExpr = expr.getChild(0);
            INode more = expr.getChild(1);
            Object left = calExpr(leftExpr);

            while(more.hasChild()) {
                INode relOp = more.getChild(0);
                INode rightExpr = more.getChild(1);
                more = more.getChild(2);

                Object right = calExpr(rightExpr);
                INode op = relOp.getChild(0);
                left = binaryOp(op, left, right);
            }

            return left;
        } else if (nodeSymbol.equals(LALRNonterminalSymbol.FACTOR)) {

        }

        throw new Exception("Unknown expression");
    }

    private Object binaryOp(INode op, Object left, Object right) throws Exception {
        GrammarSymbol opSymbol = op.getSymbol();

//        // add op
//        if (opSymbol.equals(TokenTag.SUM)) {
//            // +
//            return left + right;
//        } else if (opSymbol.equals(TokenTag.SUB)) {
//            // -
//            return left - right;
//        }
//
//        // multiply op
//        if (opSymbol.equals(TokenTag.MULTIPLY)) {
//            // *
//            return left * right;
//        } else if (opSymbol.equals(TokenTag.DIVIDE)) {
//            // /
//            return left / right;
//        }
//
//        // compare op
//        if (opSymbol.equals(TokenTag.LESS_THAN)) {
//            // <
//            return left < right;
//        } else if (opSymbol.equals(TokenTag.GREATER_THAN)) {
//            // >
//            return left > right;
//        } else if (opSymbol.equals(TokenTag.EQ)) {
//            // ==
//            return left == right;
//        } else if (opSymbol.equals(TokenTag.NEQ)) {
//            // <>
//            return left != right;
//        } else if (opSymbol.equals(TokenTag.LEQ)) {
//            // <=
//            return left <= right;
//        } else if (opSymbol.equals(TokenTag.GEQ)) {
//            // >=
//            return left >= right;
//        }

        throw new Exception("Unknown operation");
    }
}

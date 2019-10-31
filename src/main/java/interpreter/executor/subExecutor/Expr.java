package interpreter.executor.subExecutor;

import interpreter.executor.BaseExecutor;
import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.TokenTag;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.type.DataType;

public class Expr extends BaseExecutor {

    public Expr(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.EXPR)) {
            throw new Exception("parse error in expression at line " +
                    root.getAttribute(INode.INodeKey.LINE));
        }

        INode relExpr = root.getChild(0);

        return calExpr(relExpr);
    }

    private Object[] calExpr(INode expr) throws Exception {
        GrammarSymbol nodeSymbol = expr.getSymbol();

        if (nodeSymbol.equals(LALRNonterminalSymbol.RELATIONAL_EXPR)
        || nodeSymbol.equals(LALRNonterminalSymbol.SIMPLE_EXPR)
        || nodeSymbol.equals(LALRNonterminalSymbol.TERM)) {
            // relational, add / subtract, multiplication or division expression calculation.
            // left-associative operation.
            INode leftExpr = expr.getChild(0);
            INode more = expr.getChild(1);
            Object[] left = calExpr(leftExpr);

            while(more.hasChild()) {
                INode relOp = more.getChild(0);
                INode rightExpr = more.getChild(1);
                more = more.getChild(2);

                Object[] right = calExpr(rightExpr);
                INode op = relOp.getChild(0);
                left = binaryOp(op, left, right);
            }

            return left;
        } else if (nodeSymbol.equals(LALRNonterminalSymbol.FACTOR)) {
            Object[] result = new Object[2];
            int childSize = expr.getChildren().size();
            if(childSize == 1){// factor->number
                INode number = expr.getChild(0);
                INode digit = number.getChild(0);
                if (digit.getSymbol().equals(TokenTag.INTEGER)){
                    result[0] = DataType.PredefinedType.TYPE_INT;
                }else if(digit.getSymbol().equals(TokenTag.REAL_NUMBER)){
                    result[0] = DataType.PredefinedType.TYPE_REAL;
                }
                result[1] = env.findSymTblEntry((String) digit.getAttribute(INode.INodeKey.NAME)).getValue(SymTbl.SymTblKey.VALUE);
                return result;
            }else if(childSize == 3){// factor -> ( expr )
                return (Object[]) executeNode(expr.getChild(1));
            }else if(childSize == 2){
                INode preFix = expr.getChild(0);
                if(preFix.getSymbol().equals(TokenTag.SUB)){//factor -> - factor
                  result = calExpr(expr.getChild(1));
                  DataType dataType = (DataType) result[0];
                  if(!env.whileCompatible(dataType)){

                  }

                }
            }
        }

        throw new Exception("Unknown expression");
    }

    private Object[] binaryOp(INode op, Object left, Object right) throws Exception {
        GrammarSymbol opSymbol = op.getSymbol();

        // add op
        if (opSymbol.equals(TokenTag.SUM)) {
            // +
            return left + right;
        } else if (opSymbol.equals(TokenTag.SUB)) {
            // -
            return left - right;
        }

        // multiply op
        if (opSymbol.equals(TokenTag.MULTIPLY)) {
            // *
            return left * right;
        } else if (opSymbol.equals(TokenTag.DIVIDE)) {
            // /
            return left / right;
        }

        // compare op
        if (opSymbol.equals(TokenTag.LESS_THAN)) {
            // <
            return left < right;
        } else if (opSymbol.equals(TokenTag.GREATER_THAN)) {
            // >
            return left > right;
        } else if (opSymbol.equals(TokenTag.EQ)) {
            // ==
            return left == right;
        } else if (opSymbol.equals(TokenTag.NEQ)) {
            // <>
            return left != right;
        } else if (opSymbol.equals(TokenTag.LEQ)) {
            // <=
            return left <= right;
        } else if (opSymbol.equals(TokenTag.GEQ)) {
            // >=
            return left >= right;
        }

        throw new Exception("Unknown operation");
    }
}

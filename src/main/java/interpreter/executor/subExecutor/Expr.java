package interpreter.executor.subExecutor;

import interpreter.exception.SemanticError;
import interpreter.executor.BaseExecutor;
import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.TokenTag;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.env.Env;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.BasicType;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.TypeForm;

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

            while (more.hasChild()) {
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
            if (childSize == 1) {// factor->number
                INode number = expr.getChild(0);
                INode digit = number.getChild(0);
                if (digit.getSymbol().equals(TokenTag.INTEGER)) {
                    result[0] = DataType.PredefinedType.TYPE_INT;
                } else if (digit.getSymbol().equals(TokenTag.REAL_NUMBER)) {
                    result[0] = DataType.PredefinedType.TYPE_REAL;
                }
                result[1] = env.findSymTblEntry((String) digit.getAttribute(INode.INodeKey.NAME)).getValue(SymTbl.SymTblKey.VALUE);
                return result;
            } else if (childSize == 3) {// factor -> ( expr )
                return (Object[]) executeNode(expr.getChild(1));
            } else if (childSize == 2) {
                INode preFix = expr.getChild(0);
                if (preFix.getSymbol().equals(TokenTag.SUB)) {//factor -> - factor
                    result = calExpr(expr.getChild(1));
                    DataType dataType = (DataType) result[0];
                    if (!env.whileCompatible(dataType)) {
                        throw SemanticError.newWrongNegativeTpye(dataType, (Integer) expr.getChild(1).getAttribute(INode.INodeKey.LINE));
                    }
                    double res = (double) result[1];
                    result[1] = -res;
                    return result;
                }
                if (preFix.getSymbol().equals(TokenTag.IDENTIFIER)) {// factor-> identifier more-identifier
                    INode more = expr.getChild(1);
                    int moreChildSize = more.getChildren().size();
                    if (moreChildSize == 0) {// prefix is identifier
                        SymTblEntry symTblEntry = env.findSymTblEntry((String) preFix.getAttribute(INode.INodeKey.NAME));
                        result[0] = symTblEntry.getValue(SymTbl.SymTblKey.TYPE);
                        result[1] = symTblEntry.getValue(SymTbl.SymTblKey.VALUE);
                        return result;
                    }else if(moreChildSize == 2){
                        SymTblEntry symTblEntry = env.findSymTblEntry((String) preFix.getAttribute(INode.INodeKey.NAME));
                        SymTbl symTbl = (SymTbl) symTblEntry.getValue(SymTbl.SymTblKey.SYMTBL);
                        //TODO () [expr] (param-values)
                    }
                }
            }
        }

        throw new Exception("Unknown expression");
    }

    private Object[] binaryOp(INode op, Object left, Object right) throws Exception {
        GrammarSymbol opSymbol = op.getSymbol();
        Object[] LeftArray = (Object[]) left;
        Object[] RightArray = (Object[]) right;
        DataType leftType = (DataType) LeftArray[0];
        DataType rightType = (DataType) RightArray[0];
        Object[] result = new Object[2];

        //尝试进行类型转换

        if ((leftType.getBasicType().equals(BasicType.REAL) && rightType.getBasicType().equals(BasicType.REAL)) ||
                (leftType.getBasicType().equals(BasicType.REAL) && rightType.getBasicType().equals(BasicType.INT)) ||
                (leftType.getBasicType().equals(BasicType.INT) && rightType.getBasicType().equals(BasicType.REAL)) ||
                (leftType.getBasicType().equals(BasicType.INT) && rightType.getBasicType().equals(BasicType.INT))) {

            if (leftType.getBasicType().equals(BasicType.INT) && rightType.getBasicType().equals(BasicType.INT)) {
                int leftValue = Integer.parseInt((String) LeftArray[1]);
                int rightValue = Integer.parseInt((String) RightArray[1]);
                // add op
                if (opSymbol.equals(TokenTag.SUM) || opSymbol.equals(TokenTag.SUB) || opSymbol.equals(TokenTag.MULTIPLY) || opSymbol.equals(TokenTag.DIVIDE))
                {
                    // +
                    DataType elementType = new DataType(BasicType.INT, TypeForm.SCALAR);
                    result[0] = elementType;
                    if (opSymbol.equals(TokenTag.SUM)) result[1] = leftValue + rightValue;
                    if (opSymbol.equals(TokenTag.SUB)) result[1] = leftValue - rightValue;
                    if (opSymbol.equals(TokenTag.MULTIPLY)) result[1] = leftValue * rightValue;
                    if (opSymbol.equals(TokenTag.DIVIDE)) result[1] = leftValue / rightValue;
                    return result;
                }
                else
                {
                    result[0] = DataType.PredefinedType.TYPE_INT;
                    // compare op
                    if (opSymbol.equals(TokenTag.LESS_THAN)) {
                        if (leftValue < rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    } else if (opSymbol.equals(TokenTag.GREATER_THAN)) {
                        // >
                        if (leftValue > rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    } else if (opSymbol.equals(TokenTag.EQ)) {
                        // ==
                        if (leftValue == rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    } else if (opSymbol.equals(TokenTag.NEQ)) {
                        // <>
                        if (leftValue != rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    } else if (opSymbol.equals(TokenTag.LEQ)) {
                        // <=
                        if (leftValue <= rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    } else if (opSymbol.equals(TokenTag.GEQ)) {
                        // >=
                        if (leftValue >= rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    }
                    return result;
                }
            } else {
                double leftValue = Double.parseDouble((String) LeftArray[1]);
                double rightValue = Double.parseDouble((String) RightArray[1]);
                if (opSymbol.equals(TokenTag.SUM) || opSymbol.equals(TokenTag.SUB) || opSymbol.equals(TokenTag.MULTIPLY) || opSymbol.equals(TokenTag.DIVIDE))
                {
                    // +
                    DataType elementType = new DataType(BasicType.INT, TypeForm.SCALAR);
                    result[0] = elementType;
                    if (opSymbol.equals(TokenTag.SUM)) result[1] = leftValue + rightValue;
                    if (opSymbol.equals(TokenTag.SUB)) result[1] = leftValue - rightValue;
                    if (opSymbol.equals(TokenTag.MULTIPLY)) result[1] = leftValue * rightValue;
                    if (opSymbol.equals(TokenTag.DIVIDE)) result[1] = leftValue / rightValue;
                    return result;
                }
                else
                {
                    result[0] = DataType.PredefinedType.TYPE_INT;
                    // compare op
                    if (opSymbol.equals(TokenTag.LESS_THAN)) {
                        if (leftValue < rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    } else if (opSymbol.equals(TokenTag.GREATER_THAN)) {
                        // >
                        if (leftValue > rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    } else if (opSymbol.equals(TokenTag.EQ)) {
                        // ==
                        if (leftValue == rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    } else if (opSymbol.equals(TokenTag.NEQ)) {
                        // <>
                        if (leftValue != rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    } else if (opSymbol.equals(TokenTag.LEQ)) {
                        // <=
                        if (leftValue <= rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    } else if (opSymbol.equals(TokenTag.GEQ)) {
                        // >=
                        if (leftValue >= rightValue) {
                            result[1] = 1;
                        } else {
                            result[1] = 0;
                        }
                    }
                    return result;
                }
            }

        } else {
            //TODO 报错
        }


        throw new Exception("Unknown operation");
    }
}

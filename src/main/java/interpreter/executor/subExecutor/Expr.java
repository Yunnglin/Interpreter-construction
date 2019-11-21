package interpreter.executor.subExecutor;

import interpreter.exception.ExecutionError;
import interpreter.exception.SemanticError;
import interpreter.executor.BaseExecutor;
import interpreter.executor.signal.ForceExitSIgnal;
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
import interpreter.utils.INodeUtils;

import java.util.ArrayList;

public class Expr extends BaseExecutor {

    public Expr(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal, ForceExitSIgnal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.EXPR)) {
            throw new Exception("parse error in expression at line " +
                    root.getAttribute(INode.INodeKey.LINE));
        }

        INode relExpr = root.getChild(0);

        return calExpr(relExpr);
    }

    private Object[] calExpr(INode expr) throws Exception, ReturnStmt.ReturnSignal, ForceExitSIgnal {
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
                result[1] = digit.getAttribute(INode.INodeKey.VALUE);
                return result;
            } else if (childSize == 3) {// factor -> ( expr )
                return (Object[]) executeNode(expr.getChild(1));
            } else if (childSize == 2) {
                INode preFix = expr.getChild(0);
                if (preFix.getSymbol().equals(TokenTag.SUB)) {//factor -> - factor
                    result = calExpr(expr.getChild(1));
                    DataType dataType = (DataType) result[0];
                    if (!env.getTypeSystem().whileCompatible(dataType)) {
                        throw SemanticError.newWrongNegativeTpye(dataType, (Integer) expr.getChild(1).getAttribute(INode.INodeKey.LINE));
                    }
                    Double res = Double.parseDouble(result[1].toString());
                    if(dataType.getBasicType().equals(BasicType.INT)){
                        result[1] = -res.intValue();
                    }else if(dataType.getBasicType().equals(BasicType.REAL)){
                        result[1] = -res;
                    }


                    return result;
                }
                if (preFix.getSymbol().equals(TokenTag.IDENTIFIER)) {// factor-> identifier more-identifier
                    String idName = (String) preFix.getAttribute(INode.INodeKey.NAME);
                    SymTblEntry symTblEntry = env.findSymTblEntry(idName);
                    if (symTblEntry == null) {
                        throw SemanticError.newSymbolUndeclaredError(idName,
                                (Integer) preFix.getAttribute(INode.INodeKey.LINE));
                    }
                    // get symbol type
                    DataType idType = (DataType) symTblEntry.getValue(SymTbl.SymTblKey.TYPE);
                    INode more = expr.getChild(1);
                    int moreChildSize = more.getChildren().size();
                    if (moreChildSize == 0) {// prefix is identifier

                        result[0] = symTblEntry.getValue(SymTbl.SymTblKey.TYPE);
                        result[1] = symTblEntry.getValue(SymTbl.SymTblKey.VALUE);
                        return result;
                    }else if(moreChildSize == 2){
                        // () no param call
                        INode[] emptyParams = new INode[0];
                        FuncCaller caller = new FuncCaller(env);
                        return caller.callFunc(idName, emptyParams, preFix);
                    } else if(moreChildSize == 3) {
                        // ( param-values ) | [expr]
                        if (more.getChild(0).getSymbol().equals(TokenTag.L_SQUARE_BRACKETS)) {
                            // return an element of array
                            Object[] indexExpr = (Object[]) executeNode(more.getChild(1));
                            DataType indexType = (DataType) indexExpr[0];
                            // check type
                            if (!idType.getForm().equals(TypeForm.ARRAY)) {
                                // not a array
                                throw SemanticError.newWrongSubscriptedType(indexType,
                                        (Integer) more.getChild(1).getAttribute(INode.INodeKey.LINE));
                            }
                            if (!indexType.equals(DataType.PredefinedType.TYPE_INT)) {
                                // not a integer index
                                throw SemanticError.newNonIntegerArrayIndexError(idName,
                                        (Integer) more.getChild(1).getAttribute(INode.INodeKey.LINE));
                            }
                            Integer idx = (Integer) indexExpr[1];
                            Integer size = (Integer) symTblEntry.getValue(SymTbl.SymTblKey.ARRAY_SIZE);
                            Object[] values = (Object[]) symTblEntry.getValue(SymTbl.SymTblKey.VALUE);
                            if (idx < 0 || idx>=size) {
                                // out of bound
                                throw ExecutionError.newBadArrayBoundError(idName,
                                        (Integer) more.getChild(1).getAttribute(INode.INodeKey.LINE));
                            }

                            Object[] exprValue = new Object[2];
                            // TODO scalar only now, may add a new column element type
                            exprValue[0] = new DataType(idType.getBasicType(), TypeForm.SCALAR);
                            exprValue[1] = values[idx];
                            return exprValue;
                        } else {
                            // return the return value of call function
                            // make params a list
                            INode paramValues = more.getChild(1);
                            ArrayList<INode> params = INodeUtils.getLeftMostNodes(paramValues);
                            int size = params.size();
                            // call function
                            FuncCaller caller = new FuncCaller(env);
                            return caller.callFunc(idName, (INode[]) params.toArray(new INode[size]), preFix);
                        }
                    }
                }
            }
        }

        throw new Exception("Unknown expression");
    }

    public Object[] binaryOp(INode op, Object left, Object right) throws Exception {
        GrammarSymbol opSymbol = op.getSymbol();
        Object[] LeftArray = (Object[]) left;
        Object[] RightArray = (Object[]) right;
        DataType leftType = (DataType) LeftArray[0];
        DataType rightType = (DataType) RightArray[0];
        Object[] result = new Object[2];

        //尝试进行类型转换
        if (leftType.getForm().equals(TypeForm.ARRAY)||rightType.getForm().equals(TypeForm.ARRAY))
        {
            throw ExecutionError.newWrongOpeTypeError(leftType,rightType,(Integer) op.getAttribute(INode.INodeKey.LINE));
        }
        if ((leftType.getBasicType().equals(BasicType.REAL) && rightType.getBasicType().equals(BasicType.REAL)) ||
                (leftType.getBasicType().equals(BasicType.REAL) && rightType.getBasicType().equals(BasicType.INT)) ||
                (leftType.getBasicType().equals(BasicType.INT) && rightType.getBasicType().equals(BasicType.REAL)) ||
                (leftType.getBasicType().equals(BasicType.INT) && rightType.getBasicType().equals(BasicType.INT))) {
            if (leftType.getBasicType().equals(BasicType.INT) && rightType.getBasicType().equals(BasicType.INT)) {
                int leftValue = (Integer) LeftArray[1];
                int rightValue = (Integer)RightArray[1];
                // add op
                if (opSymbol.equals(TokenTag.SUM) || opSymbol.equals(TokenTag.SUB) || opSymbol.equals(TokenTag.MULTIPLY) || opSymbol.equals(TokenTag.DIVIDE))
                {
                    if(opSymbol.equals(TokenTag.DIVIDE)&&rightValue==0)
                    {
                        throw ExecutionError.newDivByZeroError((Integer) op.getAttribute(INode.INodeKey.LINE));
                    }
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

                double leftValue = Double.valueOf( LeftArray[1].toString());
                double rightValue = Double.valueOf( RightArray[1].toString());
                if (opSymbol.equals(TokenTag.SUM) || opSymbol.equals(TokenTag.SUB) || opSymbol.equals(TokenTag.MULTIPLY) || opSymbol.equals(TokenTag.DIVIDE))
                {
                    if(opSymbol.equals(TokenTag.DIVIDE)&&rightValue==0)
                    {
                        throw ExecutionError.newDivByZeroError((Integer) op.getAttribute(INode.INodeKey.LINE));
                    }
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
            //错误操作类型
            throw ExecutionError.newWrongOpeTypeError(leftType,rightType,(Integer) op.getAttribute(INode.INodeKey.LINE));
        }
    }
}
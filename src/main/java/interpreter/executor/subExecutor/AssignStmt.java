package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.exception.ExecutionError;
import interpreter.exception.SemanticError;
import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.grammar.TokenTag;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.BasicType;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.TypeForm;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AssignStmt extends BaseExecutor {
    public AssignStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.ASSIGN_STMT)) {
            throw new Exception("parse error in assign stmt");
        }
        //获取当前符号表
        ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
        INode identifierNode = root.getChild(0);
        //获取标识符的名称
        String idName = (String) identifierNode.getAttribute(INode.INodeKey.NAME);
        //获取标识符的类型
        SymTblEntry entry = env.findSymTblEntry(idName);
        //判断赋值语句第一个节点是否为标识符
        if (identifierNode.getSymbol().equals(TokenTag.IDENTIFIER)) {
            INode otherAssignNode = root.getChild(1);
            ArrayList<INode> AssignNodeList = otherAssignNode.getChildren();
            int AssignListLength = AssignNodeList.size();
            // 当前赋值变量已经存在，只是为他重新赋值
            if (entry != null) {
                DataType NodeType = (DataType) entry.getValue(SymTbl.SymTblKey.TYPE);
                //other-assign->=expr;
                if (AssignListLength == 3) {
                    Executor exe = factory.getExecutor(AssignNodeList.get(1), env);
                    Object[] exeValue = (Object[]) exe.Execute(AssignNodeList.get(1));
                    DataType exeResultType = (DataType) exeValue[0];
                    //如果标识符与表达式的结果类型匹配
                    if (env.getTypeSystem().assignCompatible(NodeType, exeResultType)) {
                        entry.addValue(SymTbl.SymTblKey.VALUE, exeValue[1]);
                       return exeValue[1];
                    }
                    //若不匹配则抛出错误
                    else {
                        throw ExecutionError.newAssignUnmatchTypeError(NodeType, exeResultType,
                                (Integer) AssignNodeList.get(1).getAttribute(INode.INodeKey.LINE));
                    }
                }
                //other-assign->[expr]=expr;
                else if (AssignListLength == 6) {
                    //判断标识符的类型是否是数组
                        if (NodeType.getForm().equals(TypeForm.ARRAY)) {
                            Executor exe = factory.getExecutor(AssignNodeList.get(1), env);
                            Object[] exeValue1 = (Object[]) exe.Execute(AssignNodeList.get(1));
                            DataType exe1Type = (DataType) exeValue1[0];

                            //判断索引的类型是否是整数
                            if (exe1Type.getBasicType().equals(BasicType.INT)) {
                                //确定索引大小
                                int indexNum = (int) exeValue1[1];
                                //执行表达式二
                                Object[] exeValue2 = (Object[]) exe.Execute(AssignNodeList.get(4));
                                DataType exeResultType = (DataType) exeValue2[0];
                                DataType elementType= new DataType(NodeType.getBasicType(),TypeForm.SCALAR);
                                //判断类型是否匹配
                                if (env.getTypeSystem().assignCompatible(elementType, exeResultType)) {
                                    //判断索引是否越界,首先获取节点的数组size
                                    int arrayLength = (int) entry.getValue(SymTbl.SymTblKey.ARRAY_SIZE);
                                    //如果索引大于等于0且小于等于length-1
                                    if (indexNum >= 0 && (indexNum + 1) <= arrayLength)
                                    {
                                        Object[] array = (Object[]) entry.getValue(SymTbl.SymTblKey.VALUE);
                                        array[indexNum]=exeValue2[1];
                                        return exeValue2[1];
                                    } else
                                    {
                                        throw ExecutionError.newBadArrayBoundError(idName, (Integer) root.getAttribute(INode.INodeKey.LINE));
                                    }

                                }
                                //若不匹配则抛出错误
                                else {
                                    throw ExecutionError.newAssignUnmatchTypeError(NodeType, exeResultType, (Integer) AssignNodeList.get(1).getAttribute(INode.INodeKey.LINE));
                                }
                            } else {
                                throw SemanticError.newReadWrongTypeError(exe1Type, (Integer) identifierNode.getAttribute(INode.INodeKey.LINE));
                            }
                    } else {
                        throw SemanticError.newWrongSubscriptedType(NodeType, (Integer) identifierNode.getAttribute(INode.INodeKey.LINE));
                    }
                } else //如果entry为空，则表示未声明就使用
                {
                    throw SemanticError.newSymbolUndeclaredError(idName, (Integer) identifierNode.getAttribute(INode.INodeKey.LINE));
                }
            } else//当前变量不存在
            {
                throw SemanticError.newSymbolUndeclaredError(idName, (Integer) identifierNode.getAttribute(INode.INodeKey.LINE));
            }
        } else {
            System.out.println("parse error in assign stmt");
            return null;
        }

    }
}

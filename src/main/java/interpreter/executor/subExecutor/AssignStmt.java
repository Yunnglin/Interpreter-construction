package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;

import java.util.ArrayList;

public class AssignStmt extends BaseExecutor {
    public AssignStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.ASSIGN_STMT)) {
            System.out.println("parse error in assign stmt");
            return null;
        }
        //获取当前符号表
        ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
        INode identifierNode = root.getChild(0);
        //获取标识符的类型
        String NodeType = (String) identifierNode.getAttribute(INode.INodeKey.NAME);
        //获取标识符的名称
        String idName = (String) identifierNode.getAttribute(INode.INodeKey.NAME);
        SymTblEntry entry = env.findSymTblEntry(idName);
        //判断赋值语句第一个节点是否为标识符
        if (identifierNode.getSymbol().equals(LALRNonterminalSymbol.MORE_IDENTIFIER))
        {
            INode otherAssignNode = root.getChild(1);
            ArrayList<INode> AssignNodeList = otherAssignNode.getChildren();
            int AssignListLength = AssignNodeList.size();
            // 当前赋值变量已经存在，只是为他重新赋值
            if (entry != null)
            {
                //other-assign->=expr;
                if (AssignListLength==3)
                {
                    Executor exe=factory.getExecutor(AssignNodeList.get(1),env);
                    Object[] exeValue= (Object[]) exe.Execute(AssignNodeList.get(1));
                    entry.addValue(SymTbl.SymTblKey.VALUE,exeValue[1]);

                }
                //other-assign->[expr]=expr;
                else if(AssignListLength==6)
                {
                    Executor exe=factory.getExecutor(AssignNodeList.get(1),env);
                    Object[] exeValue1= (Object[]) exe.Execute(AssignNodeList.get(1));
                    Object[] exeValue2= (Object[]) exe.Execute(AssignNodeList.get(4));

                    entry.addValue(SymTbl.SymTblKey.VALUE,exeValue2[1]);
                }
                else
                {

                }

            } else//当前变量不存在
            {


            }


        } else
        {
            System.out.println("parse error in assign stmt");
            return null;
        }


        return null;
    }
}

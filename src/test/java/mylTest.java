import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.utils.INodeUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class mylTest {

    @Test
    public void hello(){
        List<String> lists = new ArrayList<>();

        System.out.println("hello");
    }

    @Test
    public void test1(){
        INode stlist1 = new INode(LALRNonterminalSymbol.STMT_LIST);
        INode stlist2 = new INode(LALRNonterminalSymbol.STMT_LIST);
        INode stlist3 = new INode(LALRNonterminalSymbol.STMT_LIST);
        INode stmt1 = new INode(LALRNonterminalSymbol.STMT);
        INode stmt2 = new INode(LALRNonterminalSymbol.STMT);
        INode stmt3 = new INode(LALRNonterminalSymbol.STMT);
        stlist1.addChild(stmt1);
        stlist1.addChild(stlist2);
        stlist2.addChild(stmt2);
        stlist2.addChild(stlist3);
        stlist3.addChild(stmt3);
        ArrayList<INode> nodes = INodeUtils.getLeftMostNodes(stlist1);
        for (INode node:nodes
             ) {
            System.out.println(node.getSymbol().getSelfText());
        }
    }
    @Test
    public void test2(){
        int[] x={1,2,3};
        System.out.println(x.toString());

    }
}

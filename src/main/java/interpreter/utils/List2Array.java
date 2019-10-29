package interpreter.utils;

import interpreter.intermediate.node.INode;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class List2Array {
    public static ArrayList<INode> getArray(INode node){
        ArrayList<INode> nodes = new ArrayList<>();
        INode curNode = node;
        while (curNode!=null){
            nodes.add(curNode.getChildren().get(0));
            int len = curNode.getChildren().size();
            if (curNode.getChildren().get(len-1).getSymbol().equals(
                    curNode.getSymbol())) {
                curNode = curNode.getChildren().get(len-1);
            } else {
                curNode = null;
            }
        }
        return nodes;
    }
}

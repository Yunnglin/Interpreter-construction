package interpreter.executor;
import interpreter.intermediate.node.INode;

public interface Executor
{
    public Object Execute(INode root) throws Exception;
}

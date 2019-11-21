package interpreter.executor;
import interpreter.executor.signal.ForceExitSIgnal;
import interpreter.executor.subExecutor.ReturnStmt;
import interpreter.intermediate.node.INode;

public interface Executor
{
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal, ForceExitSIgnal;
}

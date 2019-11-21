package interpreter.executor.subExecutor;

import interpreter.debugger.StepFlag;
import interpreter.executor.BaseExecutor;
import interpreter.env.Env;
import interpreter.executor.signal.ForceExitSIgnal;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import message.Message;

public class Stmt extends BaseExecutor {

    public Stmt(Env env){
        super(env);
    }
    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal, ForceExitSIgnal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.STMT)) {
            throw new Exception("parse error in stmt");
        }

        // if execution is forced to exit
        if (Thread.currentThread().isInterrupted()) {
            throw new ForceExitSIgnal((Integer) root.getAttribute(INode.INodeKey.LINE));
        }

        Integer line = (Integer) root.getAttribute(INode.INodeKey.LINE);
        if (env.shouldStopExecution(line)) {
            // encounter a trap
            onTrap(line);
        }
        // execute the statement
        return executeNode(root.getChild(0));
    }

    private void onTrap(int line) throws InterruptedException {
        // encounter a trap
        Message message = new Message(Message.MessageType.SUSPEND_ON_TRAP, line);
        new Thread(() -> {
            postMessage(message);
        }).start();

        // wait next action
        env.stopCurExecution(line);
    }
}

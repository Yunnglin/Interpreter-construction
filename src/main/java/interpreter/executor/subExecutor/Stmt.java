package interpreter.executor.subExecutor;

import interpreter.debugger.StepFlag;
import interpreter.executor.BaseExecutor;
import interpreter.env.Env;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import message.Message;

public class Stmt extends BaseExecutor {

    public Stmt(Env env){
        super(env);
    }
    @Override
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.STMT)) {
            throw new Exception("parse error in stmt");
        }

        Integer line = (Integer) root.getAttribute(INode.INodeKey.LINE);
        if (env.shouldStopExecution(line)) {
            // encounter a trap
            onTrap(line);
        }
        // execute the statement
        // choose whether to stop according step flag
        boolean onDebug = env.isOnDebug();
        StepFlag curStepFlag = env.getCurStepFlag();
        if (onDebug && curStepFlag.equals(StepFlag.STEP_OVER)) {
            // ignore debug during the statement execution
            env.setOnDebug(false);
            Object exeResult = executeNode(root.getChild(0));
            env.setOnDebug(true);
            return exeResult;
        } else {
            return executeNode(root.getChild(0));
        }
    }

    private void onTrap(int line) throws InterruptedException {
        // encounter a trap
        Message message = new Message(Message.MessageType.SUSPEND_ON_TRAP, line);
        new Thread(() -> {
            postMessage(message);
        });

        // wait next action
        env.stopCurExecution(line);
    }
}

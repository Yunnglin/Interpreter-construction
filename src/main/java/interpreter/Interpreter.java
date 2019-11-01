package interpreter;

import interpreter.env.Env;
import interpreter.exception.ExecutionError;
import interpreter.exception.SemanticError;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.executor.subExecutor.FuncCaller;
import interpreter.executor.subExecutor.ReturnStmt;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.parser.Parser;
import message.Message;
import message.MessageHandler;
import message.MessageListener;
import message.MessageProducer;

public class Interpreter implements MessageProducer {

    private MessageHandler ioHandler;
    private Env env;
    private Parser parser;
    private INode root;

    public Interpreter(Parser parser) {
        this.parser = parser;
        this.ioHandler = new MessageHandler();
        this.env = new Env();
    }

    public Integer interpret() {
        // set root node
        if (root == null) {
            root = parser.parse();
        } else {
            this.env = new Env();
        }

        // the process exit status
        Integer exitStatusCode;

        try {
            // start to execute
            long exeStartTime = System.currentTimeMillis();

            // do interpreting with params
            // currently set params to be empty
            exitStatusCode = executeRoot(root, new INode[0]);

            // execution ends
            double exeElapsedTime = (System.currentTimeMillis() - exeStartTime) / 1000.0;
            Object[] messageBody = new Object[] {exeElapsedTime, exitStatusCode};
            Message exeMessage = new Message(Message.MessageType.INTERPRETER_SUMMARY, messageBody);
            sendMessage(exeMessage);

        } catch (SemanticError se) {
            System.out.println(se);
            Message semanticError = new Message(Message.MessageType.SEMANTIC_ERROR, se);
            this.sendMessage(semanticError);
            exitStatusCode = -1;
        } catch (ExecutionError ee) {
            System.out.println(ee);
            Message executionError = new Message(Message.MessageType.EXECUTION_ERROR, ee);
            this.sendMessage(executionError);
            exitStatusCode = -1;
        } catch (ReturnStmt.ReturnSignal | Error | Exception syse) {
            System.out.println(syse);
            Message systemError = new Message(Message.MessageType.SYS_ERROR, syse);
            this.sendMessage(systemError);
            exitStatusCode = -1000;
        }

        return exitStatusCode;
    }

    @Override
    public void addMessageListener(MessageListener listener) {
        ioHandler.addListener(listener);
        parser.addMessageListener(listener);
    }

    @Override
    public void removeMessageListener(MessageListener listener) {
        ioHandler.removeListener(listener);
        parser.removeMessageListener(listener);
    }

    @Override
    public void sendMessage(Message message) {
        ioHandler.sendMessage(message);
    }

    private Integer executeRoot(INode root, INode[] params) throws Exception, ReturnStmt.ReturnSignal {
        // get executor and execute external declarations
        ExecutorFactory executorFactory = ExecutorFactory.getExecutorFactory();
        Executor rootExecutor = executorFactory.getExecutor(root, env);
        rootExecutor.Execute(root);
        // main entrance
        SymTblEntry mainEntry = env.getMainEntry();

        // check if entrance exists
        if (mainEntry == null) {
            throw ExecutionError.newProgEntranceNotFound(Env.defaultMainEntranceName);
        } else {
            // call entrance function with params
            FuncCaller caller = new FuncCaller(env);
            Object[] values = caller.callFunc(mainEntry.getName(), params, root);
            return (Integer) values[1];
        }
    }
}

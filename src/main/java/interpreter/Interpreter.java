package interpreter;

import interpreter.env.Env;
import interpreter.exception.ExecutionError;
import interpreter.exception.SemanticError;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.executor.subExecutor.FuncCaller;
import interpreter.executor.subExecutor.ReturnStmt;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.lexer.Lexer;
import interpreter.parser.Parser;
import message.Message;
import message.MessageHandler;
import message.MessageListener;
import message.MessageProducer;

import java.io.BufferedReader;

public class Interpreter implements MessageProducer {

    private MessageHandler interpretHandler;
    private Env env;
    private Parser parser;
    private INode root;

    /**
     * Constructor with parser
     * @param parser the parser responsible for parsing
     */
    public Interpreter(Parser parser) {
        this.parser = parser;
        this.interpretHandler = new MessageHandler();
        this.env = new Env();
    }

    /**
     * Constructor with lexer, will construct parser automatically
     * @param lexer the lexer responsible for lexing
     */
    public Interpreter(Lexer lexer) {
        this(new Parser(lexer));
    }

    /**
     * Constructor with the reader of source file
     * Lexer and parser will be constructed automatically
     * @param reader buffered reader of source file
     */
    public Interpreter(BufferedReader reader) {
        this(new Parser(reader));
    }

    /**
     * do interpreting
     * @return An Integer that represents the exit status of this process
     */
    public Integer interpret() {
        // set root node
        if (root == null) {
            root = parser.parse();
        } else {
            // reset the environment
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

    /**
     * add a listener to all component
     * including parser(contains lexer), env(responsible for IO), interpreter
     * @param listener
     */
    @Override
    public void addMessageListener(MessageListener listener) {
        parser.addMessageListener(listener);
        env.addMessageListener(listener);
        interpretHandler.addListener(listener);
    }

    /**
     * remove a listener from all component if it exists in list
     * including parser(contains lexer), env(responsible for IO), interpreter
     * @param listener
     */
    @Override
    public void removeMessageListener(MessageListener listener) {
        parser.removeMessageListener(listener);
        env.removeMessageListener(listener);
        interpretHandler.removeListener(listener);
    }

    /**
     * remove a listener from the list of lexer only
     * @param listener
     */
    public void removeLexMessageListener(MessageListener listener) {
        parser.removeLexMessageListener(listener);
    }

    /**
     * remove a listener from the list of parser only
     * @param listener
     */
    public void removeParseMessageListener(MessageListener listener) {
        parser.removeParseMessageListener(listener);
    }

    /**
     * send a message to listeners for current interpreter component
     * @param message
     */
    @Override
    public void sendMessage(Message message) {
        interpretHandler.sendMessage(message);
    }

    /**
     * execute with the root node of syntax tree
     * @param root INode, the root of syntax tree got from parsing
     * @param params the arguments for entrance function
     * @return An Integer returned from the entrance function, represents exit status
     * @throws Exception
     * @throws ReturnStmt.ReturnSignal
     */
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

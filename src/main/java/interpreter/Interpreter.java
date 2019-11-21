package interpreter;

import interpreter.debugger.Breakpoint;
import interpreter.env.Env;
import interpreter.exception.ExecutionError;
import interpreter.exception.SemanticError;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.executor.signal.ForceExitSIgnal;
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

import java.io.Reader;
import java.util.ArrayList;

public class Interpreter implements MessageProducer {

    private MessageHandler interpretHandler;
    private Env env;
    private Parser parser;
    private INode root;

    /**
     * Constructor with a built environment and a parser
     * @param curEnv Env, a built environment
     * @param parser Parser, the parser responsible for parsing
     */
    public Interpreter(Env curEnv, Parser parser) {
        this.env = curEnv;
        this.parser = parser;
        this.interpretHandler = new MessageHandler();
    }

    /**
     * Constructor with parser
     * @param parser the parser responsible for parsing
     */
    public Interpreter(Parser parser) {
        this(new Env(), parser);
    }

    /**
     * Constructor with a built env and a parser
     * @param curEnv Env, a built environment
     * @param lexer Lexer, the lexer responsible for lexing
     */
    public Interpreter(Env curEnv, Lexer lexer) {
        this(curEnv, new Parser(lexer));
    }

    /**
     * Constructor with lexer, will construct parser automatically
     * @param lexer the lexer responsible for lexing
     */
    public Interpreter(Lexer lexer) {
        this(new Env(), lexer);
    }

    /**
     * Constructor with a built environment and a reader of source file
     * @param curEnv Env, a built environment
     * @param reader reader reader of source file
     */
    public Interpreter(Env curEnv, Reader reader) {
        this(curEnv, new Parser(reader));
    }

    /**
     * Constructor with the reader of source file
     * Lexer and parser will be constructed automatically
     * @param reader reader of source file
     */
    public Interpreter(Reader reader) {
        this(new Env(), reader);
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
            Message semanticError = new Message(Message.MessageType.SEMANTIC_ERROR, se);
            this.sendMessage(semanticError);
            exitStatusCode = -1;
            System.out.println(se.getMessage());
            se.printStackTrace();
        } catch (ExecutionError ee) {
            Message executionError = new Message(Message.MessageType.EXECUTION_ERROR, ee);
            this.sendMessage(executionError);
            exitStatusCode = -1;
            System.out.println(ee.getMessage());
            ee.printStackTrace();
        } catch (ReturnStmt.ReturnSignal missRet) {
            Message missRetError = new Message(Message.MessageType.SYS_ERROR, missRet);
            this.sendMessage(missRetError);
            exitStatusCode = -1001;
            System.out.println(missRet.getMessage());
            missRet.printStackTrace();
        } catch (Error | Exception syse) {
            String syseMessage = syse.getMessage() == null ? syse.getClass().getSimpleName() : syse.getMessage();
            Message systemError = new Message(Message.MessageType.SYS_ERROR, new Exception(syseMessage));
            this.sendMessage(systemError);
            exitStatusCode = -1000;
            System.out.println(syse);
            syse.printStackTrace();
        } catch (ForceExitSIgnal forceExitSIgnal) {
            String exitMessage = forceExitSIgnal.getMessage();
            Message forceExit = new Message(Message.MessageType.FORCE_EXIT, exitMessage);
            this.sendMessage(forceExit);
            exitStatusCode = -1002;
            System.out.println(forceExitSIgnal.getMessage());
            forceExitSIgnal.printStackTrace();
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
    private Integer executeRoot(INode root, INode[] params) throws Exception, ReturnStmt.ReturnSignal, ForceExitSIgnal {
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

    public void stepIn() {
        this.env.stepIn();
    }

    public void stepOver() {
        this.env.stepOver();
    }

    public void continueExecution() {
        this.env.continueExecution();
    }

    public boolean addBreakpoint(Breakpoint breakpoint) {
        return this.env.addBreakpoint(breakpoint);
    }

    public boolean removeBreakpoint(Breakpoint breakpoint) {
        return this.env.removeBreakpoint(breakpoint);
    }

    /**
     * initialize the debugger and turn it on
     * @param breakpoints ArrayList of Breakpoint
     */
    public void initDebugger(ArrayList<Breakpoint> breakpoints) {
        this.env.initDebugger(breakpoints);
    }

    /**
     * find the symbol table entry for the specified name in available scope
     * @param symbolName String, the name of symbol
     * @return SymTblEntry
     */
    public SymTblEntry findSymTblEntry(String symbolName) {
        return this.env.findSymTblEntry(symbolName);
    }

}

package interpreter.parser;

import interpreter.Const;
import interpreter.grammar.TokenTag;
import interpreter.exception.SyntaxError;
import interpreter.grammar.GrammarSymbol;
import interpreter.grammar.NonterminalSymbol;
import interpreter.grammar.Production;
import interpreter.grammar.TerminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.node.INode.INodeKey;
import interpreter.lexer.Lexer;
import interpreter.lexer.token.IntNum;
import interpreter.lexer.token.Real;
import interpreter.lexer.token.Token;
import interpreter.lexer.token.Word;
import interpreter.grammar.lalr.*;
import interpreter.grammar.lalr.state.LALRParseManager;
import interpreter.grammar.lalr.state.LALRStateMachine;
import message.Message;
import message.MessageListener;
import message.MessageProducer;
import message.MessageHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser implements MessageProducer {
    private static boolean update = false;

    private MessageHandler parseHandler;
    private LALRParseManager parseManager;
    private Lexer lexer;
    private ArrayList<Token> tokens;

    /**
     * add a listener to all components
     * including lexer and parser
     * @param listener
     */
    @Override
    public void addMessageListener(MessageListener listener) {
        this.lexer.addMessageListener(listener);
        this.parseHandler.addListener(listener);
    }

    /**
     * remove a listener from all components if it exits in the list
     * including lexer and parser
     * @param listener
     */
    @Override
    public void removeMessageListener(MessageListener listener) {
        this.lexer.removeMessageListener(listener);
        this.parseHandler.removeListener(listener);
    }

    /**
     * remove the listener from the list of lexer
     * @param listener
     */
    public void removeLexMessageListener(MessageListener listener) {
        this.lexer.removeMessageListener(listener);
    }

    /**
     * remove the listener from the list of parser only
     * @param listener
     */
    public void removeParseMessageListener(MessageListener listener) {
        this.parseHandler.removeListener(listener);
    }

    /**
     * send a message to all listeners in current parser component
     * @param message
     */
    @Override
    public void sendMessage(Message message) {
        this.parseHandler.sendMessage(message);
    }

    /**
     * Constructor with lexer
     * @param lexer the lexer responsible for lexing
     */
    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.parseHandler = new MessageHandler();
        if (update) {
            this.parseManager = new LALRParseManager();
        } else {
            String instancePath;
            if (LALRGrammar.mode.equals("terminal")) {
                // use the parse manager for terminal
                instancePath = Const.terminalParseManagerInstancePath;
            } else {
                // use the default parse manager
                instancePath = Const.parseManagerInstancePath;
            }
            File file = new File(instancePath);
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                this.parseManager = (LALRParseManager) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                // cannot read the parseManager object file
                // or the file have been out of date
                // TODO handle the error properly ?
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Constructor with reader of source file
     * will construct lexer automatically
     * @param reader reader of source file
     */
    public Parser(Reader reader) {
        this(new Lexer(reader));
    }

    /**
     * start lexing and parsing, and send the information to listeners
     */
    public INode parse() {
        try {
            // measure how much time lexer spent
            long lexStartTime = System.currentTimeMillis();

            if (tokens == null) {
                tokens = lexer.lex();
            }

            // measure how much time parser spent
            long parseStartTime = System.currentTimeMillis();
            // parser part
            INode root = parseTokens(tokens);
            double parseElapsedTime = (System.currentTimeMillis() - parseStartTime) / 1000.0;
            Object[] parseMsgBody = new Object[] {parseElapsedTime, root};
            this.sendMessage(new Message(Message.MessageType.PARSER_SUMMARY, parseMsgBody));

            return root;
        } catch (SyntaxError syntaxError) {
            // catch a syntax error when parsing
            Message errorMsg = new Message(Message.MessageType.SYNTAX_PARSE_ERROR, syntaxError);
            this.sendMessage(errorMsg);
            syntaxError.printStackTrace();
        } catch (Exception | Error syse) {
            // catch a system error
            Message systemError = new Message(Message.MessageType.SYS_ERROR, syse);
            this.sendMessage(systemError);
            System.out.println(syse.getMessage());
            syse.printStackTrace();
        }

        return null;
    }

    /**
     * do parsing
     * @param tokens the token produced after lexing is done
     * @return the root node of syntax tree
     * @throws SyntaxError
     */
    private INode parseTokens(ArrayList<Token> tokens) throws SyntaxError {
        LALRGrammar grammar = LALRGrammar.getGrammar();
        INode root = null;

        // prepare two stacks, one for symbols and the other for states
        // use the number of tokens as initial capacity
        ArrayList<GrammarSymbol> symbolStack = new ArrayList<>(tokens.size());
        ArrayList<Integer> stateStack = new ArrayList<>(tokens.size());
        symbolStack.add(TokenTag.PROG_END);
        stateStack.add(0);
        int symbolTop = 0;
        int tokenTop = 0;

        ArrayList<INode> curChildren = new ArrayList<>();
        while(tokenTop < tokens.size()) {
            Token token = tokens.get(tokenTop);
            TerminalSymbol symbol = token.getTag();
            Integer action = parseManager.getAction(stateStack.get(symbolTop), symbol);

            if (action == null) {
                // the action is error
//                if (token.getTag().equals(TokenTag.PROG_END)) {
//                    // early end
//                    throw SyntaxError.newMissingTokenError( tokens.get(tokenTop-1),
//                            getExpectedTokenTag(stateStack.get(symbolTop)));
//                }
                System.out.println("symbol: " + symbol);
                Integer state = stateStack.get(symbolTop);
                System.out.println("" + state + parseManager.getState(state));
                throw SyntaxError.newUnexpectedTokenError(token);
//                ArrayList<TokenTag> expectedTokens = getExpectedTokenTag(state);
//                if (expectedTokens.size() > 0) {
//                    throw SyntaxError.newMissingTokenError(tokens.get(tokenTop-1), expectedTokens);
//                } else {
//                    throw SyntaxError.newUnexpectedTokenError(token);
//                }
            }

            if (action == 0) {
                // accept
                NonterminalSymbol startSymbol = grammar.getStartSymbol();
                root = new INode(startSymbol);
                for (int i=0; i<=symbolTop-1; ++i) {
                    INode child = curChildren.get(i);
                    root.addChild(child);
                }
            } else if (action > 0) {
                // shift
                Integer targetId = action - 1;
                ++symbolTop;
                // generate a new node
                INode newNode = new INode(symbol);

                if (symbolTop >= symbolStack.size()) {
                    symbolStack.add(symbol);
                    stateStack.add(targetId);
                    curChildren.add(newNode);
                } else {
                    symbolStack.set(symbolTop, symbol);
                    stateStack.set(symbolTop, targetId);
                    curChildren.set(symbolTop-1, newNode);
                }

                // set attribute
                if (symbol.equals(TokenTag.INTEGER)) {
                    newNode.setAttribute(INodeKey.VALUE, ((IntNum)token).getValue());
                } else if (symbol.equals(TokenTag.REAL_NUMBER)) {
                    newNode.setAttribute(INodeKey.VALUE, ((Real) token).getValue());
                } else if (token instanceof Word && (symbol.equals(TokenTag.IDENTIFIER) ||
                        lexer.isKeyWord(((Word) token)))) {
                    newNode.setAttribute(INodeKey.NAME, ((Word) token).getLexeme());
                }

                newNode.setAttribute(INodeKey.LINE, token.getLineNum());
            } else {
                --tokenTop;
                // reduce
                Integer targetId = Math.abs(action) - 1;
                Production production = grammar.getProduction(targetId);
                ArrayList<GrammarSymbol> right = production.getRightSymbols();
                GrammarSymbol left = production.getLeft();
                INode newNode = new INode(left);
                int oldSymbolTop = symbolTop;
                // stack pops items
                for (int i=right.size()-1; i>=0; --i) {
                    if (!right.get(i).equals(LALRGrammar.NIL)) {
                        if (!symbolStack.get(symbolTop).equals(right.get(i))) {
                            // there are some problems in the transition table
                            System.out.println("reduce: " + "An error escaped the check of transition table");
                        }
                        --symbolTop;
                    }
                }
                // add children to new node
                for (int i=(symbolTop+1); i<=oldSymbolTop; ++i) {
                    newNode.addChild(curChildren.get(i-1));
                }

                // set attribute
                newNode.setAttribute(INodeKey.LINE,
                        newNode.hasChild() ? newNode.getChild(0).getAttribute(INodeKey.LINE) : 0);

                // push the symbol into stack
                Integer curState = stateStack.get(symbolTop);
                Integer gotoId = parseManager.getAction(curState, left) - 1;

                ++symbolTop;
                if (symbolTop >= symbolStack.size()) {
                    symbolStack.add(left);
                    stateStack.add(gotoId);
                    curChildren.add(newNode);
                } else {
                    symbolStack.set(symbolTop, left);
                    stateStack.set(symbolTop, gotoId);
                    curChildren.set(symbolTop-1, newNode);
                }
            }

            // the token stack pops
            ++tokenTop;
        }

        if (root == null) {
            System.out.println("missing tokens: " + "An error escaped the check of transition table");
            throw SyntaxError.newMissingTokenError( tokens.get(tokenTop-1),
                    getExpectedTokenTag(stateStack.get(symbolTop)));
        }

        return root;
    }

    /**
     * when a syntax error occurred, get the expected type of tokens for current state
     * @param curState the id of current state
     * @return the list of expected tag of token
     */
    private ArrayList<TokenTag> getExpectedTokenTag(Integer curState) {
        ArrayList<TokenTag> expected = new ArrayList<TokenTag>();
        LALRStateMachine stateMachine = parseManager.getStateMachine();
        ArrayList<HashMap<GrammarSymbol, Integer>> transitionTable = stateMachine.getTransitionTable();
        HashMap<GrammarSymbol, Integer> row = transitionTable.get(curState);
        LALRGrammar grammar = LALRGrammar.getGrammar();

        for (GrammarSymbol symbol : row.keySet()) {
            if (grammar.isTerminalSymbol(symbol.getSelfText()) && row.get(symbol) > 0) {
                // a terminal symbol and action is shift
                expected.add((TokenTag) symbol);
            }
        }

        return expected;
    }
}

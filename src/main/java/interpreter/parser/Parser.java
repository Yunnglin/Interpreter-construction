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

    private MessageHandler handler;
    private LALRParseManager parseManager;
    private Lexer lexer;

    @Override
    public void addMessageListener(MessageListener listener) {
        this.handler.addListener(listener);
    }

    @Override
    public void removeMessageListener(MessageListener listener) {
        this.handler.removeListener(listener);
    }

    @Override
    public void sendMessage(Message message) {
        this.handler.sendMessage(message);
    }

    /**
     * Constructor
     * @param lexer the lexer that have not done lexing
     */
    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.handler = new MessageHandler();
        if (update) {
            this.parseManager = new LALRParseManager();
        } else {
            File file = new File(Const.parseManagerInstancePath);
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                this.parseManager = (LALRParseManager) ois.readObject();
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * start lexing and parsing, and send the information to listeners
     */
    public void parse() {
        try {
            // measure how much time lexer spent
            long lexStartTime = System.currentTimeMillis();

            // lexer part
            ArrayList<Token> tokens = lexer.getAllToken();
            // lexical part finished, sending summary message.
            float lexElapsedTime = (System.currentTimeMillis() - lexStartTime) / 1000f;
            Object[] lexMsgBody = new Object[] {lexElapsedTime, tokens};
            this.sendMessage(new Message(Message.MessageType.LEXER_SUMMARY, lexMsgBody));

            // measure how much time parser spent
            long parseStartTime = System.currentTimeMillis();
            // parser part
            INode root = parseTokens(tokens);
            float parseElapsedTime = (System.currentTimeMillis() - parseStartTime) / 1000f;
            Object[] parseMsgBody = new Object[] {parseElapsedTime, root};
            this.sendMessage(new Message(Message.MessageType.PARSER_SUMMARY, parseMsgBody));
        } catch (IOException e) {
            Message errorMsg = new Message(Message.MessageType.IO_ERROR, e);
            this.sendMessage(errorMsg);
            e.printStackTrace();
        } catch (SyntaxError syntaxError) {
            Message errorMsg = new Message(Message.MessageType.SYNTAX_ERROR, syntaxError);
            this.sendMessage(errorMsg);
            syntaxError.printStackTrace();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
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
                System.out.println("symbol: " + symbol);
                System.out.println("" + stateStack.get(symbolTop) + parseManager.getState(stateStack.get(symbolTop)));
                throw SyntaxError.newUnexpectedTokenError(token);
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
            if (grammar.isTerminalSymbol(symbol.getSelfText())) {
                expected.add((TokenTag) symbol);
            }
        }

        return expected;
    }
}

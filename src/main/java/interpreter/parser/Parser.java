package interpreter.parser;

import interpreter.Const.TokenTag;
import interpreter.exception.SyntaxError;
import interpreter.intermediate.node.INode;
import interpreter.lexer.Lexer;
import interpreter.lexer.token.Token;
import interpreter.utils.lalr.*;
import interpreter.utils.lalr.state.LALRParseManager;
import message.Message;
import message.MessageListener;
import message.MessageProducer;
import message.MessageHandler;

import java.io.IOException;
import java.util.ArrayList;

public class Parser implements MessageProducer {

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

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.handler = new MessageHandler();
        this.parseManager = LALRParseManager.getInstance();
    }

    public void parse() {
        try {
            // measure how much time lexer spent
            long startTime = System.currentTimeMillis();

            // lexer part
            ArrayList<Token> tokens = lexer.getAllToken();
            // lexical part finished, sending summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime) / 1000f;
            Object[] msgBody = new Object[] {elapsedTime, tokens};
            handler.sendMessage(new Message(Message.MessageType.LEXER_SUMMARY, msgBody));

            // parser part
        } catch (IOException e) {
            Message errorMsg = new Message(Message.MessageType.IO_ERROR, e);
            handler.sendMessage(errorMsg);
            e.printStackTrace();
        } catch (SyntaxError syntaxError) {
            Message errorMsg = new Message(Message.MessageType.SYNTAX_ERROR, syntaxError);
            handler.sendMessage(errorMsg);
            syntaxError.printStackTrace();
        }
    }

    private INode parseTokens(ArrayList<Token> tokens) throws SyntaxError {
        LALRGrammar grammar = parseManager.getGrammar();
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
                throw SyntaxError.newUnexpectedTokenError(token);
            }

            if (action == 0) {
                // accept
                NonterminalSymbol startSymbol = grammar.getStartSymbol();
                root = new INode(startSymbol);
                for (INode child : curChildren) {
                    root.addChild(child);
                }
            } else if (action > 0) {
                // shift
                Integer targetId = action - 1;
                ++symbolTop;
                if (symbolTop >= symbolStack.size()) {
                    symbolStack.add(symbol);
                    stateStack.add(targetId);
                } else {
                    symbolStack.set(symbolTop, symbol);
                    stateStack.set(symbolTop, targetId);
                }

                // generate a new node
                INode newNode = new INode(symbol);
                curChildren.add(newNode);

                // set attribute
//                switch ()
            } else {
                // reduce
                Integer targetId = Math.abs(action) - 1;
                Production production = grammar.getProduction(targetId);
                ArrayList<GrammarSymbol> right = production.getRightSymbols();
                GrammarSymbol left = production.getLeft();

            }

            // the token stack pops
            ++tokenTop;
        }

        if (root == null) {
            System.out.println("An error escaped the check of transition table");
            throw SyntaxError.newMissingTokenError( tokens.get(tokenTop-1),
                    getExpectedTokenTag(stateStack.get(symbolTop)));
        }

        return root;
    }

    private ArrayList<TokenTag> getExpectedTokenTag(Integer curState) {
        ArrayList<TokenTag> expected = new ArrayList<TokenTag>();

        return expected;
    }
}

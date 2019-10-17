package interpreter.parser;

import interpreter.Const.TokenTag;
import interpreter.exception.SyntaxError;
import interpreter.intermediate.node.INode;
import interpreter.lexer.Lexer;
import interpreter.lexer.token.Token;
import interpreter.utils.lalr.GrammarSymbol;
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

    private INode parseTokens(ArrayList<Token> tokens) {
        INode root = new INode();

        // prepare two stacks, one for symbols and the other for states
        ArrayList<GrammarSymbol> symbolStack = new ArrayList<>();
        ArrayList<Integer> stateStack = new ArrayList<>();
        symbolStack.add(TokenTag.PROG_END);
        stateStack.add(0);
        int symbolTop = 0;
        int tokenTop = 0;

        while(true) {
        }

        return root;
    }
}

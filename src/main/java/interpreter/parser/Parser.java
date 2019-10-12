package interpreter.parser;

import message.Message;
import message.MessageListener;
import message.MessageProducer;
import message.MessageHandler;

public class Parser implements MessageProducer {

    private MessageHandler handler;

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
}

package message;

import java.util.ArrayList;

public class MessageHandler {
    private ArrayList<MessageListener> listeners;

    /**
     * Constructor
     */
    public MessageHandler() {
        this.listeners = new ArrayList<MessageListener>();
    }

    public void addListener(MessageListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(MessageListener listener) {
        this.listeners.remove(listener);
    }

    public void sendMessage(Message message) {
        for (MessageListener listener : this.listeners) {
            listener.onMessageReceived(message);
        }
    }

}

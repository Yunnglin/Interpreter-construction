package message;

public interface MessageListener {
    /**
     * The action taken on receiving a message
     * @param message
     */
    public void onMessageReceived(Message message);
}

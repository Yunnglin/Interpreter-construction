package message;

public interface MessageProducer {
    /**
     * Add a listener
     * @param listener
     */
    public void addMessageListener(MessageListener listener);

    /**
     * remove a listener
     * @param listener
     */
    public void removeMessageListener(MessageListener listener);

    /**
     * broadcast a message
     * @param message
     */
    public void sendMessage(Message message);
}

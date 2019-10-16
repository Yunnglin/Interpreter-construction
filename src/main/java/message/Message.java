package message;

public class Message {
    public enum MessageType {
        SROUCE_CHAR, TOKEN, PARSER_SUMMARY, INTERPRETER_SUMMARY, LEXER_SUMMARY
    }

    private MessageType type;
    private Object body;

    /**
     * Constructor
     * @param type the type of the message
     * @param body the content of the message
     */
    public Message(MessageType type, Object body) {
        this.type = type;
        this.body = body;
    }

    public MessageType getType() {
        return type;
    }

    public Object getBody() {
        return body;
    }
}

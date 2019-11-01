package message;

public class Message {
    public enum MessageType {
        SROUCE_CHAR, TOKEN, PARSER_SUMMARY, INTERPRETER_SUMMARY, LEXER_SUMMARY,
        SYNTAX_ERROR, IO_ERROR, READ_INPUT,WRITE, EXECUTION_ERROR, SEMANTIC_ERROR
    }

    private MessageType type;
    private Object body;

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", body=" + body +
                '}';
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    /**
     * Constructor
     */
    public Message() {}

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

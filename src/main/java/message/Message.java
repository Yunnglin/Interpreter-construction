package message;

import java.util.concurrent.locks.Condition;

public class Message {
    public enum MessageType {
        PARSER_SUMMARY, INTERPRETER_SUMMARY, LEXER_SUMMARY, SYS_ERROR, SYNTAX_PARSE_ERROR,
        SYNTAX_LEX_ERROR, IO_ERROR, READ_INPUT,WRITE, EXECUTION_ERROR, SEMANTIC_ERROR,
        SUSPEND_ON_TRAP, FORCE_EXIT
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

package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.exception.SemanticError;
import interpreter.executor.BaseExecutor;
import interpreter.grammar.TokenTag;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.DataType;
import interpreter.intermediate.type.TypeForm;
import interpreter.lexer.token.Token;
import message.Message;
import message.Message.MessageType;

public class ReadStmt extends BaseExecutor {

    public ReadStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.READ_STMT)) {
            throw new Exception("parse error in read stmt at line " +
                    root.getAttribute(INode.INodeKey.LINE));
        }
        // read identifier | read identifier [expr]
        INode identifier = root.getChild(1);
        INode more = root.getChild(2);
        String idName = (String) identifier.getAttribute(INode.INodeKey.NAME);
        SymTblEntry entry = env.findSymTblEntry(idName);
        if (entry == null) {
            // used before declare
            throw SemanticError.newSymbolUndeclaredError(idName,
                    (Integer) identifier.getAttribute(INode.INodeKey.LINE));
        }
        DataType type = (DataType) entry.getValue(SymTbl.SymTblKey.TYPE);

        if (more.getSymbol().equals(TokenTag.SEMICOLON)) {
            // terminated, only read a identifier (scalar)
            if (!type.getForm().equals(TypeForm.SCALAR)) {
                // required scalar
                throw SemanticError.newReadWrongTypeError(type,
                        (Integer) identifier.getAttribute(INode.INodeKey.LINE));
            }

            // start read
            Message input = read();
            if (input.getType().equals(MessageType.IO_ERROR)) {
                // if encountered io error
            }

            return input.getBody();
        } else {
            // not terminated, read to a array element
            if (!type.getForm().equals(TypeForm.ARRAY)) {
                throw SemanticError.newWrongSubscriptedType(type,
                        (Integer) identifier.getAttribute(INode.INodeKey.LINE));
            }

            // start read
            Message input = read();
            if (input.getType().equals(MessageType.IO_ERROR)) {
                // if encountered io error
            }

            return input.getBody();
        }
    }

    private Message read() throws InterruptedException {
        Object[] body = new Object[] {};
        Message message = new Message(Message.MessageType.READ_INPUT, body);
        new Thread(() -> {
            postMessage(message);
        }).start();

        // wait the input
        synchronized (message) {
            message.wait();
        }

        return message;
    }

}

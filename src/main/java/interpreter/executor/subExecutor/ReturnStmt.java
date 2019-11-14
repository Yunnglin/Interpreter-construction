package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.executor.BaseExecutor;
import interpreter.grammar.TokenTag;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.type.DataType;

import java.util.Arrays;

public class ReturnStmt extends BaseExecutor {

    public class ReturnSignal extends Throwable {

        private Object[] retValue;
        private int line;

        @Override
        public String getMessage() {
            return "Uncaught Return Signal: " + Arrays.toString(this.getRetValue());
        }

        public ReturnSignal(int line) {
            this.retValue = new Object[] {DataType.PredefinedType.TYPE_VOID, null};
            this.line = line;
        }

        public Object[] getRetValue() {
            return retValue;
        }

        public void setRetValue(Object[] retValue) {
            this.retValue = retValue;
        }

        public ReturnSignal(Object[] retValue, int line) {
            this.retValue = retValue;
            this.line = line;
        }

        public ReturnSignal(ReturnSignal cause, int line) {
            this(cause.getRetValue(), line);
        }

        public DataType getType() {
            return (DataType) this.retValue[0];
        }

        public int getLine() {
            return line;
        }

        public void setLine(int line) {
            this.line = line;
        }
    }

    public ReturnStmt(Env env) {
        super(env);
    }

    @Override
    public Object Execute(INode root) throws Exception, ReturnSignal {
        if (!root.getSymbol().equals(LALRNonterminalSymbol.RETURN_STMT)) {
            throw new Exception("parse error in return stmt at line " +
                    root.getAttribute(INode.INodeKey.LINE));
        }

        INode more = root.getChild(1);

        if (more.getSymbol().equals(TokenTag.SEMICOLON)) {
            // terminated, return nothing
            throw new ReturnSignal((Integer) root.getAttribute(INode.INodeKey.LINE));
        } else {
            // not terminated, return a expr value
            Object[] value = (Object[]) executeNode(more);
            throw new ReturnSignal(value, (Integer) root.getAttribute(INode.INodeKey.LINE));
        }
    }
}

package interpreter.executor.subExecutor;

import interpreter.env.Env;
import interpreter.exception.ExecutionError;
import interpreter.exception.SemanticError;
import interpreter.executor.BaseExecutor;
import interpreter.executor.Executor;
import interpreter.executor.ExecutorFactory;
import interpreter.grammar.TokenTag;
import interpreter.grammar.lalr.LALRNonterminalSymbol;
import interpreter.intermediate.node.INode;
import interpreter.intermediate.sym.SymTbl;
import interpreter.intermediate.sym.SymTblEntry;
import interpreter.intermediate.type.BasicType;
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
    public Object Execute(INode root) throws Exception, ReturnStmt.ReturnSignal {
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

        if (more.getSymbol().equals(TokenTag.SEMICOLON))
        {
            // terminated, only read a identifier (scalar)
            if (!type.getForm().equals(TypeForm.SCALAR) || type.getBasicType().equals(BasicType.VOID)) {
                // required scalar
                throw SemanticError.newReadWrongTypeError(type,
                        (Integer) identifier.getAttribute(INode.INodeKey.LINE));
            }

            // start read
            Message input = read();
            if (input.getType().equals(MessageType.IO_ERROR)) {
                // if encountered io error
            }

            Object readResult;
            //尝试进行类型转换
            try {
                if (type.equals(DataType.PredefinedType.TYPE_INT)) {
                    // if identifier is an INT
                    readResult = Integer.parseInt((String)input.getBody());
                    entry.addValue(SymTbl.SymTblKey.VALUE, readResult);
                    return readResult;
                } // if identifier is a REAL
                else if (type.equals(DataType.PredefinedType.TYPE_REAL)) {
                    readResult =Double.parseDouble((String)input.getBody());
                    entry.addValue(SymTbl.SymTblKey.VALUE, readResult);
                    return readResult;
                }
            }
            catch (Exception e)
            {
                throw SemanticError.newReadWrongTypeError(type,
                        (Integer) identifier.getAttribute(INode.INodeKey.LINE));
            }
        }
        else
        //read identifier [expr] ;
        {
            // not terminated, read to a array element
            if (!type.getForm().equals(TypeForm.ARRAY)) {
                throw SemanticError.newWrongSubscriptedType(type,
                        (Integer) identifier.getAttribute(INode.INodeKey.LINE));
            }
            Message input = read();
            ExecutorFactory factory = ExecutorFactory.getExecutorFactory();
            Executor exe = factory.getExecutor(root.getChild(3), env);
            //执行结果
            Object[] exeValue1 = (Object[]) exe.Execute(root.getChild(3));
            DataType exe1Type = (DataType) exeValue1[0];
            //判断索引的类型是否是整数
            if (exe1Type.getBasicType().equals(BasicType.INT))
            {
                //确定索引大小
                int indexNum = (int) exeValue1[1];
                int arrayLength = (int) entry.getValue(SymTbl.SymTblKey.ARRAY_SIZE);
                //判断是否索引是否越界
                if (indexNum >= 0 && (indexNum + 1) <= arrayLength)
                {
                    //判断类型读入类型与数组元素类型是否匹配
                    DataType elementType = new DataType(type.getBasicType(), TypeForm.SCALAR);
                    //尝试进行类型转换
                    try {
                        Object readResult;
                        if (type.getBasicType().equals(BasicType.INT)) {
                            // if identifier is an INT
                            readResult =Integer.parseInt((String)input.getBody());
                            Object[] array = (Object[]) entry.getValue(SymTbl.SymTblKey.VALUE);
                            array[indexNum] = readResult;
                            return readResult;

                        } // if identifier is a REAL
                        else if (type.getBasicType().equals(BasicType.REAL)) {
                            readResult = Double.parseDouble((String)input.getBody());
                            Object[] array = (Object[]) entry.getValue(SymTbl.SymTblKey.VALUE);
                            array[indexNum] = readResult;
                            return readResult;
                        }
                    }
                    catch (Exception e)
                    {
                        throw SemanticError.newReadWrongTypeError(type,
                                (Integer) identifier.getAttribute(INode.INodeKey.LINE));
                    }

                }
                else
                    {
                        throw ExecutionError.newBadArrayBoundError(idName, (Integer) root.getAttribute(INode.INodeKey.LINE));
                    }
            }
            else
            {
                throw SemanticError.newReadWrongTypeError(exe1Type, (Integer) root.getChild(4).getAttribute(INode.INodeKey.LINE));
            }
            // start read

            if (input.getType().equals(MessageType.IO_ERROR)) {
                // if encountered io error
            }

        }
        return null;
    }

    private Message read() throws InterruptedException {
        Object[] body = new Object[]{};
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

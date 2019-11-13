package exception;

import cmm.*;

public class OpCheck {
	public static void check(SimpleNode thisNode) throws SemanticError {
		SimpleNode child0 = (SimpleNode) thisNode.jjtGetChild(0);
		SimpleNode child1 = (SimpleNode) thisNode.jjtGetChild(1);
		if(child0 instanceof ASTScalarId) {
			Object val = MyNode.symtab.get(((ASTScalarId) child0).name);
			Token token = ((ASTScalarId) child0).firstToken;
			if (val == null) {
				throw SemanticError.newIdentifierNotDefinedError(token);
			}
			if(val instanceof int[] || val instanceof double[]) {
				throw SemanticError.newArrayPointerExecuteError(token);
			}
		}
		
		if(child1 instanceof ASTScalarId) {
			Object val = MyNode.symtab.get(((ASTScalarId) child1).name);
			Token token = ((ASTScalarId) child1).firstToken;
			if (val == null) {
				throw SemanticError.newIdentifierNotDefinedError(token);
			}
			if(val instanceof int[] || val instanceof double[]) {
				throw SemanticError.newArrayPointerExecuteError(token);
			}
		}
	}
}

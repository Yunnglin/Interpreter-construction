package exception;

import cmm.Token;

public class SemanticError extends Error {
	
	String msg;
	int line;
	int column;
	String image;
	
	@Override
	public String getMessage() {
		return this.msg+" At line: "+this.line+" column: "+this.column+ " For Image \"" +this.image+"\"";
	}

	@Override
	public String toString() {
		return getMessage();
	}
	public SemanticError(String msg) {
		this.msg = msg;
	}
	
	public SemanticError(String msg,int line,int column) {
		this.msg = msg;
		this.line = line;
		this.column = column;
	}
	
	public SemanticError(String msg,Token token) {
		this.msg = msg;
		this.line = token.beginLine;
		this.column = token.beginColumn;
		this.image = token.image;
	}
	
    public static SemanticError newNegativeArraySizeError(Token token) {
        return new SemanticError("SIZE OF ARRAY  IS NEGATIVE",token);
    }
    
    public static SemanticError newTypeNotMatchError(Token token) {
    	return new SemanticError("TYPE NOT MATCH",token);
    }
    
    public static SemanticError newIdentifierNotDefinedError(Token token) {
    	return new SemanticError("IDENTIFIER  NOT DEFINED",token);
    }
    
    public static SemanticError newBoundryOutOfIndexError(Token token) {
    	return new SemanticError("BOUNDRY OUT OF INDEX",token);
    }
    
    public static SemanticError newArrayPointerExecuteError(Token token) {
    	return new SemanticError("ARRAY POINTER CANN'T BE POERATED",token);
    }
}

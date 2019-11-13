package exception;

public class SemanticError extends Error {
	
	String msg;
	@Override
	public String getMessage() {
		return this.msg;
	}

	@Override
	public String toString() {
		return getMessage();
	}
	public SemanticError(String msg) {
		this.msg = msg;
	}
    public static SemanticError newNegativeArraySizeError() {
        return new SemanticError("SIZE OF ARRAY  IS NEGATIVE");
    }
    
    public static SemanticError newTypeNotMatchError() {
    	return new SemanticError("Type Not Match");
    }
    
    public static SemanticError newIdentifierNotDefinedError() {
    	return new SemanticError("Identifier Not Defined");
    }
    
    public static SemanticError newBoundryOutOfIndexError() {
    	return new SemanticError("Boundry Out Of Index");
    }
}

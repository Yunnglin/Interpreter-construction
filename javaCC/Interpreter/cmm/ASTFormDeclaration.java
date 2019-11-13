/* Generated By:JJTree: Do not edit this line. ASTFormDeclaration.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=MyNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package cmm;
import exception.*;

public
class ASTFormDeclaration extends SimpleNode implements CMMParserConstants{
	public int type;
	public String name;
	public int count=0;
  public ASTFormDeclaration(int id) {
    super(id);
  }

  public ASTFormDeclaration(CMMParser p, int id) {
    super(p, id);
  }
  
  public void putInTable() throws SemanticError {
	  if(count<0) {
		  throw SemanticError.newNegativeArraySizeError();
	  }
	  if (type == INT) {
		  symtab.put(name, new int[count]);
	  }else if(type == REAL) {
		  symtab.put(name, new double[count]);
	  }
  }

}
/* JavaCC - OriginalChecksum=f86f2630e48ec4dfe7eb00f15e4129c1 (do not edit this line) */

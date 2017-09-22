package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public class IdentLValue extends ASTNode {
	
	private TypeName type;
	private Dec dec;
	
	public IdentLValue(Token firstToken) {
		super(firstToken);
	}
	
	@Override
	public String toString() {
		return "IdentLValue [firstToken=" + firstToken + "]";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentLValue(this,arg);
	}

	public String getText() {
		return firstToken.getText();
	}
	
	public TypeName getType() {
		return type;
	}
	
	public void setType(TypeName name) {
		this.type = name;
	}
	
	public Dec getDec() {
		return dec;
	}
	
	public void setDec(Dec d) {
		this.dec = d;
	}


}

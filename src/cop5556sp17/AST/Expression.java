package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public abstract class Expression extends ASTNode {
	
	private TypeName type;
	private int val;
	private Dec dec;
	
	protected Expression(Token firstToken) {
		super(firstToken);
	}

	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;
	
	public TypeName getType() {
		return type;
	}
	
	public void setType(TypeName name) {
		this.type = name;
	}
	
	public int getVal() {
		return val;
	}
	
	public void setVal(int value) {
		this.val = value;
	}
	
	public Dec getDec() {
		return dec;
	}
	
	public void setDec(Dec d) {
		this.dec = d;
	}

}

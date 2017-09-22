package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;


public abstract class Chain extends Statement {
	private TypeName type;
	private Dec dec;

	public Chain(Token firstToken) {
		super(firstToken);
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

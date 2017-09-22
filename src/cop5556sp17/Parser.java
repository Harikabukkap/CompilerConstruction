package cop5556sp17;

import static cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;

import java.util.ArrayList;

import cop5556sp17.Scanner.Token;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.WhileStatement;
import cop5556sp17.AST.Statement;


public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	
	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	ASTNode parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	Expression expression() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		//expression = term ( relOp term)*
		Expression e0 = null;        
		Expression e1 = null;        
		Token first = t;        
		e0 = term();        
		while (t.kind == LT || t.kind == LE || t.kind == GT || t.kind == GE || t.kind == EQUAL || t.kind == NOTEQUAL) {
			Token relOp  = t;        
			consume();
			e1 = term();        
			e0 = new BinaryExpression(first, e0,relOp,e1);        
		}	
		return e0;         
	}

	Expression term() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		//term = elem ( weakOp  elem)*
		Expression e0 = null;        
		Expression e1 = null;        
		Token first = t;        
		e0 = elem();        
		while (t.kind == PLUS || t.kind == MINUS || t.kind == OR) {
			Token weakOp = t;        
			consume();
			e1 = elem();        
			e0 = new BinaryExpression(first, e0,weakOp,e1);        
		}	
		return e0;        
	}

	Expression elem() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		//elem ∷= factor ( strongOp factor)*
		Expression e0 = null;        
		Expression e1 = null;        
		Token first = t;        
		e0 = factor();        
		while(t.kind == TIMES || t.kind == DIV || t.kind == AND || t.kind == MOD) {
			Token strongOp = t;        
			consume();
			e1 = factor();
			e0 = new BinaryExpression(first, e0,strongOp,e1);        
		}
		
		return e0;
	}

	Expression factor() throws SyntaxException {
		//factor ∷= IDENT | INT_LIT | KW_TRUE | KW_FALSE | KW_SCREENWIDTH | KW_SCREENHEIGHT | ( expression )
		Kind kind = t.kind;
		Expression e = null;        
		Token first = t;
		switch (kind) {
		case IDENT: {
			e = new IdentExpression(first);        
			consume();
		}
			break;
		case INT_LIT: {
			e = new IntLitExpression(first);        
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {
			e = new BooleanLitExpression(first);        
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			e = new ConstantExpression(first);        
			consume();
		}
			break;
		case LPAREN: {
			consume();
			e = (Expression) expression();        
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
		}
		return e;        
	}

	Block block() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		//block ::= { ( dec | statement) * }
		Token first = t;        
		Block b = null;        
		ArrayList<Dec> ald = new ArrayList<Dec>();        
		ArrayList<Statement> als = new ArrayList<Statement>();        
		if (t.kind == LBRACE) {
			
			consume();
			while(true) {
				
				if (t.kind == KW_INTEGER || t.kind == KW_BOOLEAN || t.kind == KW_IMAGE || t.kind == KW_FRAME) {	
					ald.add(dec());        
				}
				else if (t.kind == OP_SLEEP || t.kind == KW_WHILE || t.kind == KW_IF || t.kind == IDENT || t.kind == OP_BLUR || 
						t.kind == OP_GRAY || t.kind == OP_CONVOLVE || t.kind == KW_SHOW || t.kind == KW_HIDE || t.kind == KW_MOVE || t.kind == KW_XLOC || t.kind == KW_YLOC ||
						t.kind == OP_WIDTH || t.kind == OP_HEIGHT || t.kind == KW_SCALE) 
				{
					als.add(statement());        
				}	
				
				else {
					break;
				}	
				
			}
			
			match(RBRACE);
		}
		
		else throw new SyntaxException("illegal block token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
		b = new Block(first,ald,als);        
		return b;                   	
	}
	
	Program program() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		//program ::=  IDENT block & program ::=  IDENT param_dec ( , param_dec )*   block
		Program p = null; //                     
		Block b = null;        
		Token first = t;        
		ArrayList<ParamDec> alpd = new ArrayList<ParamDec>();        
		if(t.kind == IDENT) {
			consume();
			if (t.kind == KW_URL || t.kind == KW_FILE || t.kind == KW_INTEGER || t.kind == KW_BOOLEAN) {
				alpd.add(paramDec());        
				while(t.kind == COMMA) {
					consume();
					alpd.add(paramDec());        
				}	
			}
			b = block();
		}
		else throw new SyntaxException("Invalid program token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
		p = new Program(first,alpd,b);
		return p;
	}

	
	ParamDec paramDec() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		//paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN)   IDENT
		ParamDec pd = null;        
		Token type = null;        
		Token ident = null;        
		if (t.kind == KW_URL || t.kind == KW_FILE || t.kind == KW_INTEGER || t.kind == KW_BOOLEAN) { 
			type = t;        
			consume();
			if (t.kind == IDENT) {
				ident = t;        
				consume(); 
				}
			else throw new SyntaxException("Invalid paramDec token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
		}
		else throw new SyntaxException("Invalid paramDec token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
		pd = new ParamDec(type,ident);        
		return pd;        
		}

	
	 Dec dec() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		//dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT
	    Dec d = null;        
	    Token type = null;
	    Token ident = null;
		if (t.kind == KW_INTEGER || t.kind == KW_BOOLEAN || t.kind == KW_IMAGE || t.kind == KW_FRAME) {
			type = t;        
			consume();
			if (t.kind == IDENT) {
				ident = t;        
				consume(); 
				}
			else throw new SyntaxException("Invalid dec token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
		}		
		else throw new SyntaxException("Invalid dec token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
		d = new Dec(type, ident);        
		return d;        
	}

	Statement statement() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		//statement ::=   OP_SLEEP expression ; | whileStatement | ifStatement | chain ; | assign ;
		Statement s = null;        
		Token first = t;        
		Expression e = null;        
		Block b = null;        
		IdentLValue i = null;        
		switch (t.kind) {
		
		case OP_SLEEP:
			consume();
			e = expression();        
			s = new SleepStatement(first,e);        
			if(t.kind == SEMI) {
				consume();
			}
			else throw new SyntaxException("Invalid statement token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
		break;
		
		case KW_WHILE:
			consume();
			match(LPAREN);
			e = expression();
			match(RPAREN);
			b = block();
			s = new WhileStatement(first,e,b);
		break;
		
		case KW_IF:
			consume();
			match(LPAREN);
			e = expression();
			match(RPAREN);
			b = block();
			s = new IfStatement(first,e,b);
		break;
		
		case IDENT:
			Token t2 = scanner.peek();
			if (t2.kind == ASSIGN){
				i = new IdentLValue(first);        
				consume();
				consume();
				e = expression();        
				s = new AssignmentStatement(first,i,e);         
				if (t.kind == SEMI) {consume();}
				else throw new SyntaxException("Invalid statement assign token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
			}
			else {
				s = chain();        
				if (t.kind == SEMI) {consume();}
				else throw new SyntaxException("Invalid statement chain token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
			}
		break;	
	
		default:
			s = chain();        
			if (t.kind == SEMI) {consume();}
			else throw new SyntaxException("Invalid statement chain token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
		break;
		}
		
		return s;        
	}

	Chain chain() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		//chain ::=  chainElem arrowOp chainElem ( arrowOp  chainElem)*
		Token first = t;        
		Chain c = null;                              
		ChainElem ce = null;                      
		c = chainElem();
		if (t.kind == ARROW || t.kind ==  BARARROW)
		{	
			while (t.kind == ARROW || t.kind ==  BARARROW) 
			{
				Token arrowOp = t;        
				consume();
				ce = chainElem();        
				c = new BinaryChain(first,c,arrowOp,ce);        
			}
		}
		else throw new SyntaxException("Invalid chain token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
		return c;        
	}
	

	ChainElem chainElem() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		//chainElem ::= IDENT | filterOp arg | frameOp arg | imageOp arg
		ChainElem ce = null;        
		Token first = t;        
		Tuple tup = null;        
		switch(t.kind){
		
		case IDENT:
			ce = new IdentChain(first);
			consume();
		break;
		
		case OP_BLUR:
		case OP_GRAY:
		case OP_CONVOLVE:
			consume();																																																																															
			tup = arg();        
			ce = new FilterOpChain(first, tup);         
		break;
		
		case KW_SHOW:
		case KW_HIDE:
		case KW_MOVE:
		case KW_XLOC:
		case KW_YLOC:
			consume();
			tup = arg();        
			ce = new FrameOpChain(first, tup);        
		break;
		
		case OP_WIDTH:
		case OP_HEIGHT:
		case KW_SCALE:
			consume();
			tup = arg();        
			ce = new ImageOpChain(first, tup);        
		break;
		
		default:
			throw new SyntaxException("illegal chainElem token:"+ t.kind+"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line);
		
		}
		return ce;
	}

	Tuple arg() throws SyntaxException {
		//TODO
		//throw new UnimplementedFeatureException();
		//arg ::= ε | ( expression (   ,expression)* )
		Tuple tup = null;        
		ArrayList<Expression> al = new ArrayList<Expression>();        
		Token first = t;        
		if(t.kind == LPAREN)
		{	
			consume();
			al.add(expression());        
			while(t.kind == COMMA){
				consume();
				al.add(expression());        
				
				}
			match(RPAREN);
		}
		tup = new Tuple(first,al);        
		return tup;        
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF ) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.kind == kind) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind +"\t at position:"+t.getLinePos().posInLine+"\t on line:"+t.getLinePos().line+ "\t expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		for (Kind k : kinds) {
			if (t.kind == k)
				return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kinds);
		
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}

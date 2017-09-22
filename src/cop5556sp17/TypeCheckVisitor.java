package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
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
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import java.util.List;
import java.util.ArrayList;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import cop5556sp17.SymbolTable.decScopePair;
import cop5556sp17.SymbolTable;

import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;
import cop5556sp17.AST.Type.*;
import java.util.LinkedList;
import java.util.Map;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {

		Chain c = binaryChain.getE0();
		c.visit(this, arg);

		ChainElem ce = binaryChain.getE1();
		ce.visit(this, arg);

		switch(binaryChain.getArrow().kind) {

		case ARROW:

			if ((c.getType().isType(TypeName.URL) && ce.getType().isType(TypeName.IMAGE))
					|(c.getType().isType(TypeName.FILE) && ce.getType().isType(TypeName.IMAGE))
					|(c.getType().isType(TypeName.IMAGE) && ce instanceof FilterOpChain && ce.getFirstToken().isKind(OP_GRAY,OP_BLUR,OP_CONVOLVE))
					|(c.getType().isType(IMAGE) && ce instanceof ImageOpChain && ce.getFirstToken().isKind(KW_SCALE))
					|(c.getType().isType(IMAGE) && ce instanceof IdentChain && ce.getType().isType(TypeName.INTEGER))
					|(c.getType().isType(IMAGE) && ce instanceof IdentChain && ce.getType().isType(TypeName.IMAGE))){
				binaryChain.setType(TypeName.IMAGE);
			}
			else if ((c.getType().isType(TypeName.FRAME) && ce instanceof FrameOpChain && ce.getFirstToken().isKind(KW_XLOC,KW_YLOC))
					|(c.getType().isType(TypeName.IMAGE) && ce instanceof ImageOpChain && ce.getFirstToken().isKind(OP_WIDTH, OP_HEIGHT))
					|(c.getType().isType(TypeName.INTEGER) && ce instanceof IdentChain && ce.getType().isType(TypeName.INTEGER))){
				binaryChain.setType(TypeName.INTEGER);
			}
			else if ((c.getType().isType(TypeName.FRAME) && ce instanceof FrameOpChain && ce.getFirstToken().isKind(KW_SHOW,KW_HIDE, KW_MOVE))
					|(c.getType().isType(TypeName.IMAGE) && ce.getType().isType(TypeName.FRAME))){
				binaryChain.setType(TypeName.FRAME);
			}
			else if((c.getType().isType(TypeName.IMAGE) && ce.getType().isType(TypeName.FILE))){
				binaryChain.setType(TypeName.NONE);
			}
			else  {
				throw new TypeCheckException("Illegal token type encountered in binary Chain1 "+ "token:"+binaryChain.getFirstToken().getText()+'\t'+"at pos:"+binaryChain.getFirstToken().pos+'\t'+"on line:"+binaryChain.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+binaryChain.getFirstToken().getLinePos().posInLine);
			}
			break;
		case BARARROW:
			if((c.getType().isType(TypeName.IMAGE) && ce instanceof FilterOpChain && ce.getFirstToken().isKind(OP_GRAY,OP_BLUR,OP_CONVOLVE))){
				binaryChain.setType(TypeName.IMAGE);
			}
			else
				throw new TypeCheckException("Illegal token type encountered in binary Chain2 "+ "token:"+binaryChain.getFirstToken().getText()+'\t'+"at pos:"+binaryChain.getFirstToken().pos+'\t'+"on line:"+binaryChain.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+binaryChain.getFirstToken().getLinePos().posInLine);
		break;
		default:
			throw new TypeCheckException("Illegal token type encountered in binary Chain3 "+ "token:"+binaryChain.getFirstToken().getText()+'\t'+"at pos:"+binaryChain.getFirstToken().pos+'\t'+"on line:"+binaryChain.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+binaryChain.getFirstToken().getLinePos().posInLine);
		}

		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {

		Expression e0 = binaryExpression.getE0();
		e0.visit(this, arg);

		Expression e1 = binaryExpression.getE1();
		e1.visit(this, arg);

		switch(binaryExpression.getOp().kind) {
		case PLUS:
			if ((e0.getType().isType(TypeName.INTEGER) && e1.getType().isType(TypeName.INTEGER))
					|(e0.getType().isType(TypeName.IMAGE) && e1.getType().isType(TypeName.IMAGE))){
				if (e0.getType().isType(TypeName.INTEGER) && e1.getType().isType(TypeName.INTEGER)){
					binaryExpression.setType(TypeName.INTEGER);
				}
				else {
					binaryExpression.setType(TypeName.IMAGE);
				}
			}
			else
				throw new TypeCheckException("Illegal token type encountered in binaryExpression "+ "token:"+binaryExpression.getFirstToken().getText()+'\t'+"at pos:"+binaryExpression.getFirstToken().pos+'\t'+"on line:"+binaryExpression.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+binaryExpression.getFirstToken().getLinePos().posInLine);
			break;
		case MINUS:
			if ((e0.getType().isType(TypeName.INTEGER) && e1.getType().isType(TypeName.INTEGER))
					|(e0.getType().isType(TypeName.IMAGE) && e1.getType().isType(TypeName.IMAGE))){
				if (e0.getType().isType(TypeName.INTEGER)&& e1.getType().isType(TypeName.INTEGER)){
					binaryExpression.setType(TypeName.INTEGER);
				}
				else {
					binaryExpression.setType(TypeName.IMAGE);
				}
			}
			else
				throw new TypeCheckException("Illegal token type encountered in binaryExpression "+ "token:"+binaryExpression.getFirstToken().getText()+'\t'+"at pos:"+binaryExpression.getFirstToken().pos+'\t'+"on line:"+binaryExpression.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+binaryExpression.getFirstToken().getLinePos().posInLine);
			break;
		case TIMES:
			if ((e0.getType().isType(TypeName.INTEGER) && e1.getType().isType(TypeName.INTEGER))
					|(e0.getType().isType(TypeName.IMAGE) && e1.getType().isType(TypeName.INTEGER))
					|(e0.getType().isType(TypeName.INTEGER) && e1.getType().isType(TypeName.IMAGE))){
				if (e0.getType().isType(TypeName.INTEGER) && e1.getType().isType(TypeName.INTEGER)){
					binaryExpression.setType(TypeName.INTEGER);
				}
				else {
					binaryExpression.setType(TypeName.IMAGE);
				}
			}
			else
				throw new TypeCheckException("Illegal token type encountered in binaryExpression "+ "token:"+binaryExpression.getFirstToken().getText()+'\t'+"at pos:"+binaryExpression.getFirstToken().pos+'\t'+"on line:"+binaryExpression.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+binaryExpression.getFirstToken().getLinePos().posInLine);
			break;
		case DIV:
			if ((e0.getType().isType(TypeName.INTEGER) && e1.getType().isType(TypeName.INTEGER)))
			{
				binaryExpression.setType(TypeName.INTEGER);
			}
			else if ((e0.getType().isType(TypeName.IMAGE) && e1.getType().isType(TypeName.INTEGER)))
			{
				binaryExpression.setType(TypeName.IMAGE);
			}
			else
				throw new TypeCheckException("Illegal token type encountered in binaryExpression "+ "token:"+binaryExpression.getFirstToken().getText()+'\t'+"at pos:"+binaryExpression.getFirstToken().pos+'\t'+"on line:"+binaryExpression.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+binaryExpression.getFirstToken().getLinePos().posInLine);
			break;
		case LT:
		case GT:
		case LE:
		case GE:
		case OR:
		case AND:
			if ((e0.getType() .isType(TypeName.INTEGER) && e1.getType() .isType(TypeName.INTEGER))
					|(e0.getType() .isType(TypeName.BOOLEAN) && e1.getType() .isType(TypeName.BOOLEAN))){
				binaryExpression.setType(TypeName.BOOLEAN);
			}
			else
				throw new TypeCheckException("Illegal token type encountered in binaryExpression "+ "token:"+binaryExpression.getFirstToken().getText()+'\t'+"at pos:"+binaryExpression.getFirstToken().pos+'\t'+"on line:"+binaryExpression.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+binaryExpression.getFirstToken().getLinePos().posInLine);
			break;
		case EQUAL:
		case NOTEQUAL:
			if (e0.getType()== e1.getType()){
				binaryExpression.setType(TypeName.BOOLEAN);
			}
			else
				throw new TypeCheckException("Illegal token type encountered in binaryExpression "+ "token:"+binaryExpression.getFirstToken().getText()+'\t'+"at pos:"+binaryExpression.getFirstToken().pos+'\t'+"on line:"+binaryExpression.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+binaryExpression.getFirstToken().getLinePos().posInLine);
			break;
		case MOD:
			if (e0.getType().isType(IMAGE) && e1.getType().isType(INTEGER)){
				binaryExpression.setType(IMAGE);
			}
			else if (e0.getType().isType(INTEGER) && e1.getType().isType(INTEGER)) {
				binaryExpression.setType(INTEGER);
			}
			else
				throw new TypeCheckException("Illegal token type encountered in binaryExpression "+ "token:"+binaryExpression.getFirstToken().getText()+'\t'+"at pos:"+binaryExpression.getFirstToken().pos+'\t'+"on line:"+binaryExpression.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+binaryExpression.getFirstToken().getLinePos().posInLine);

		default:
		}

		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {

		symtab.enterScope();
		ArrayList<Dec> ald = block.getDecs();
		for (Dec d: ald){
			d.visit(this, arg);
		}
		ArrayList<Statement> als = block.getStatements();
		for (Statement s: als){
			s.visit(this, arg);
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {

		booleanLitExpression.setType(TypeName.BOOLEAN);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {

		Tuple t = filterOpChain.getArg();
		t.visit(this, arg);
		if (t.getExprList().size() == 0) {
			filterOpChain.setType(TypeName.IMAGE);
		}
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {

		Tuple t = frameOpChain.getArg();
		t.visit(this, arg);
		if (frameOpChain.firstToken.isKind(Kind.KW_SHOW) || frameOpChain.firstToken.isKind(Kind.KW_HIDE)) {
			if (t.getExprList().size() == 0)
				frameOpChain.setType(TypeName.NONE);
		}
		else if (frameOpChain.firstToken.isKind(Kind.KW_XLOC) || frameOpChain.firstToken.isKind(Kind.KW_YLOC)){
			if (t.getExprList().size() == 0)
				frameOpChain.setType(TypeName.INTEGER);
		}
		else if(frameOpChain.firstToken.isKind(Kind.KW_MOVE)){
			if (t.getExprList().size() == 2)
				frameOpChain.setType(TypeName.NONE);
		}
		else
			throw new TypeCheckException("Illegal token type encountered in FrameOpChain "+ "token:"+frameOpChain.getFirstToken().getText()+'\t'+"at pos:"+frameOpChain.getFirstToken().pos+'\t'+"on line:"+frameOpChain.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+frameOpChain.getFirstToken().getLinePos().posInLine);

		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		String ident = identChain.getFirstToken().getText();
		Dec d = symtab.lookup(ident);
		if (d != null){
			identChain.setType( symtab.lookup(identChain.getFirstToken().getText()).getType());
			identChain.setDec(d);
		}
		else
			throw new TypeCheckException("Illegal token type encountered in Ident Chain "+ "token:"+identChain.getFirstToken().getText()+'\t'+"at pos:"+identChain.getFirstToken().pos+'\t'+"on line:"+identChain.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+identChain.getFirstToken().getLinePos().posInLine);
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {

		String ident = identExpression.getFirstToken().getText();
		Dec d = symtab.lookup(ident);
		if (d != null) {
			identExpression.setType( d.getType() );
			identExpression.setDec(d);
		}
		else
			throw new TypeCheckException("Illegal token type encountered in Ident Expression "+ "token:"+identExpression.getFirstToken().getText()+'\t'+"at pos:"+identExpression.getFirstToken().pos+'\t'+"on line:"+identExpression.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+identExpression.getFirstToken().getLinePos().posInLine);
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {

		Expression e = ifStatement.getE();
		e.visit(this, arg);

		Block b = ifStatement.getB();
		b.visit(this, arg);

		if (e.getType().isType(TypeName.BOOLEAN)){}
		else
			throw new TypeCheckException("Illegal token type encountered in If Statement "+ "token:"+ifStatement.getFirstToken().getText()+'\t'+"at pos:"+ifStatement.getFirstToken().pos+'\t'+"on line:"+ifStatement.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+ifStatement.getFirstToken().getLinePos().posInLine);
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		intLitExpression.setType(TypeName.INTEGER);
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {

		Expression e = sleepStatement.getE();
		e.visit(this, arg);
		if (e.getType().isType(TypeName.INTEGER)){}
		else
			throw new TypeCheckException("Illegal token type encountered in Sleep Statement "+ "token:"+sleepStatement.getFirstToken().getText()+'\t'+"at pos:"+sleepStatement.getFirstToken().pos+'\t'+"on line:"+sleepStatement.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+sleepStatement.getFirstToken().getLinePos().posInLine);
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {

		Expression e = whileStatement.getE();
		e.visit(this, arg);

		Block b = whileStatement.getB();
		b.visit(this, arg);

		if (e.getType().isType(TypeName.BOOLEAN)){}
		else
			throw new TypeCheckException("Illegal token type encountered in While Statement "+ "token:"+whileStatement.getFirstToken().getText()+'\t'+"at pos:"+whileStatement.getFirstToken().pos+'\t'+"on line:"+whileStatement.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+whileStatement.getFirstToken().getLinePos().posInLine);
		return null;
	}


	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {

		String ident = declaration.getIdent().getText();
		Dec d = null;
		if ((d = symtab.lookup(ident)) != null) {
			LinkedList<decScopePair> list = symtab.map.get(ident);
			for(decScopePair p : list) {
				if (p.scope == symtab.scope_stack.peek()) {
					throw new TypeCheckException("Illegal token type encountered in Declaration Statement "+ "token:"+declaration.getFirstToken().getText()+'\t'+"at pos:"+declaration.getFirstToken().pos+'\t'+"on line:"+declaration.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+declaration.getFirstToken().getLinePos().posInLine);

				}
			}
		}
		symtab.insert(ident, declaration);
		declaration.setType(cop5556sp17.AST.Type.getTypeName(declaration.getFirstToken()));

		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		ArrayList<ParamDec> pdl = program.getParams();
		for(ParamDec pd: pdl){
			pd.visit(this, arg);
		}
		program.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {

		IdentLValue ilv = assignStatement.getVar();
		Expression e = assignStatement.getE();

		ilv.visit(this, arg);
		e.visit(this, arg);

		if (symtab.lookup(ilv.getText()).getType().isType(e.getType())) {

		}
		else {
			throw new TypeCheckException("Illegal token type encountered in Assignment Statement "+ "token:"+assignStatement.getFirstToken().getText()+'\t'+"at pos:"+assignStatement.getFirstToken().pos+'\t'+"on line:"+assignStatement.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+assignStatement.getFirstToken().getLinePos().posInLine);
		}
			return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {

		String ident = identX.getFirstToken().getText();
		Dec d = symtab.lookup(ident);
		if ( d != null ) {
			identX.setDec(d);
		}
		else
			throw new TypeCheckException("Illegal token type encountered in IdentLValue "+ "token:"+identX.getFirstToken().getText()+'\t'+"at pos:"+identX.getFirstToken().pos+'\t'+"on line:"+identX.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+identX.getFirstToken().getLinePos().posInLine);
		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {

		String ident = paramDec.getIdent().getText();
		Dec d = null;

		if ( symtab.map.get(ident) != null) {
			LinkedList<decScopePair> list = symtab.map.get(ident);
			for(decScopePair p : list) {

				if (p.scope == symtab.current_scope)
					throw new TypeCheckException("Illegal token type encountered in paramDec Statement "+ "token:"+paramDec.getFirstToken().getText()+'\t'+"at pos:"+paramDec.getFirstToken().pos+'\t'+"on line:"+paramDec.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+paramDec.getFirstToken().getLinePos().posInLine);
			}


		}
		paramDec.setType(cop5556sp17.AST.Type.getTypeName(paramDec.getFirstToken()));
		symtab.insert(ident, paramDec);

		return null;

	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {

		constantExpression.setType(TypeName.INTEGER);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {

		Tuple t = imageOpChain.getArg();
		t.visit(this, arg);
		if (imageOpChain.firstToken.isKind(Kind.OP_WIDTH) || imageOpChain.firstToken.isKind(Kind.OP_HEIGHT)) {
			if (t.getExprList().size() == 0)
				imageOpChain.setType(TypeName.INTEGER);
		}
		else if (imageOpChain.firstToken.isKind(Kind.KW_SCALE)){
			if (t.getExprList().size() == 1)
				imageOpChain.setType(TypeName.IMAGE);
		}
		else
			throw new TypeCheckException("Illegal token type encountered in ImageOpChain "+ "token:"+imageOpChain.getFirstToken().getText()+'\t'+"at pos:"+imageOpChain.getFirstToken().pos+'\t'+"on line:"+imageOpChain.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+imageOpChain.getFirstToken().getLinePos().posInLine);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {

		List<Expression> ale = tuple.getExprList();
		for (Expression e: ale){
			e.visit(this, arg);
			if (e.getType().isType(TypeName.INTEGER)){}
			else
				throw new TypeCheckException("Illegal token type encountered in Tuple "+ "token:"+tuple.getFirstToken().getText()+'\t'+"at pos:"+tuple.getFirstToken().pos+'\t'+"on line:"+tuple.getFirstToken().getLinePos().line+'\t'+"at posinLine:"+tuple.getFirstToken().getLinePos().posInLine);
		}
		return null;
	}

}

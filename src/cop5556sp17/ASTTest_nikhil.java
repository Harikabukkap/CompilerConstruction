package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.AST.*;

public class ASTTest_nikhil {

	static final boolean doPrint = true;
	static void show(Object s){
		if(doPrint){System.out.println(s);}
	}
	

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IdentExpression.class, ast.getClass());
	}

	@Test
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(IntLitExpression.class, ast.getClass());
	}



	@Test
	public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "1+abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
	}
	
	@Test
	public void testTuple() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(1+abc,5)";
        //String input1 = "  (abc * 123 + 456 * true >= abc * 123 + 456,false / abc | 456 % screenheight != abc * 123 + 456) ";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.arg();
		assertEquals(Tuple.class, ast.getClass());
		Tuple t = (Tuple) ast;
		List<Expression> list= t.getExprList();
		BinaryExpression be = (BinaryExpression)list.get(0);
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(PLUS, be.getOp().kind);
		Expression e = list.get(1);
		assertEquals(IntLitExpression.class, e.getClass());
		//System.out.println(t.toString());
		
	}
	
	@Test
	public void testTuple1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(123>=abc,screenheight,565 * 123 > 456)";
        //String input1 = "  (abc * 123 + 456 * true >= abc * 123 + 456,false / abc | 456 % screenheight != abc * 123 + 456) ";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.arg();
		assertEquals(Tuple.class, ast.getClass());
		Tuple t = (Tuple) ast;
		List<Expression> list= t.getExprList();
		BinaryExpression be = (BinaryExpression)list.get(0);
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(GE, be.getOp().kind);
		Expression exp = list.get(1);
		assertEquals(ConstantExpression.class, exp.getClass());
		
		BinaryExpression e = (BinaryExpression)list.get(2);
		assertEquals(BinaryExpression.class, e.getE0().getClass());
		assertEquals(IntLitExpression.class, e.getE1().getClass());
		assertEquals(GT, e.getOp().kind);
		BinaryExpression e1 = (BinaryExpression)e.getE0();
		assertEquals(IntLitExpression.class, e1.getE0().getClass());
		assertEquals(IntLitExpression.class, e1.getE1().getClass());
		assertEquals(TIMES, e1.getOp().kind);
		//System.out.println(t.toString());
		
	}
	@Test
	public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "565 * 123 > 456";
        //String input1 = "  (abc * 123 + 456 * true >= abc * 123 + 456,false / abc | 456 % screenheight != abc * 123 + 456) ";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(BinaryExpression.class, ast.getClass());
		BinaryExpression be = (BinaryExpression) ast;
		assertEquals(BinaryExpression.class, be.getE0().getClass());
		assertEquals(IntLitExpression.class, be.getE1().getClass());
		assertEquals(GT, be.getOp().kind);
		BinaryExpression be1 = (BinaryExpression)be.getE0();
		assertEquals(IntLitExpression.class, be1.getE0().getClass());
		assertEquals(IntLitExpression.class, be1.getE1().getClass());
		assertEquals(TIMES, be1.getOp().kind);
		//System.out.println(be.toString());
		//System.out.println(be1.toString());
		
	}
	
	@Test
	public void testExpression1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "screenwidth";
        //String input1 = "  (abc * 123 + 456 * true >= abc * 123 + 456,false / abc | 456 % screenheight != abc * 123 + 456) ";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.expression();
		assertEquals(ConstantExpression.class, ast.getClass());

		
	}
	
	@Test
	public void testExpressionError() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc * 123 %";
        //String input1 = "  (abc * 123 + 456 * true >= abc * 123 + 456,false / abc | 456 % screenheight != abc * 123 + 456) ";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		ASTNode ast = parser.expression();
	}
	
	@Test
	public void testChainElem() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "scale (123>=abc,true,565 * 123 > 456)";
        //String input1 = "  (abc * 123 + 456 * true >= abc * 123 + 456,false / abc | 456 % screenheight != abc * 123 + 456) ";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chainElem();
		assertEquals(ImageOpChain.class, ast.getClass());
		
		ImageOpChain iChain = (ImageOpChain)ast;
		Tuple t = iChain.getArg();
		List<Expression> list= t.getExprList();
		BinaryExpression be = (BinaryExpression)list.get(0);
		assertEquals(IntLitExpression.class, be.getE0().getClass());
		assertEquals(IdentExpression.class, be.getE1().getClass());
		assertEquals(GE, be.getOp().kind);
		Expression exp = list.get(1);
		assertEquals(BooleanLitExpression.class, exp.getClass());
		
		BinaryExpression e = (BinaryExpression)list.get(2);
		assertEquals(BinaryExpression.class, e.getE0().getClass());
		assertEquals(IntLitExpression.class, e.getE1().getClass());
		assertEquals(GT, e.getOp().kind);
		BinaryExpression e1 = (BinaryExpression)e.getE0();
		assertEquals(IntLitExpression.class, e1.getE0().getClass());
		assertEquals(IntLitExpression.class, e1.getE1().getClass());
		assertEquals(TIMES, e1.getOp().kind);
		//System.out.println(iChain.toString());
	}
	
	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "blur (3,5) -> hide |-> gray";
        //String input1 = "  (abc * 123 + 456 * true >= abc * 123 + 456,false / abc | 456 % screenheight != abc * 123 + 456) ";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.chain();
		assertEquals(BinaryChain.class, ast.getClass());
		
		BinaryChain bChain = (BinaryChain)ast;
		assertEquals(BinaryChain.class, bChain.getE0().getClass());
		assertEquals(FilterOpChain.class, bChain.getE1().getClass());
		assertEquals(BARARROW, bChain.getArrow().kind);
		
		BinaryChain bChain1 = (BinaryChain) bChain.getE0();
		assertEquals(FilterOpChain.class, bChain1.getE0().getClass());
		assertEquals(FrameOpChain.class, bChain1.getE1().getClass());
		assertEquals(ARROW, bChain1.getArrow().kind);
		
		FilterOpChain bChain2 = (FilterOpChain) bChain1.getE0();
		Tuple t = bChain2.getArg();
		//System.out.println(t.toString());

	}
	
	@Test
	public void testStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "sleep true * 123;";
        //String input1 = "  (abc * 123 + 456 * true >= abc * 123 + 456,false / abc | 456 % screenheight != abc * 123 + 456) ";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(SleepStatement.class, ast.getClass());
		//System.out.println(ast.toString());


	}
	
	@Test
	public void testStatement1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "blur (3,5) -> hide |-> gray;";
        //String input1 = "  (abc * 123 + 456 * true >= abc * 123 + 456,false / abc | 456 % screenheight != abc * 123 + 456) ";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(BinaryChain.class, ast.getClass());
		//System.out.println(ast.toString());


	}
	
		
	@Test
	public void testStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "nikhil <- 565 * 123 > 456;";
        //String input1 = "  (abc * 123 + 456 * true >= abc * 123 + 456,false / abc | 456 % screenheight != abc * 123 + 456) ";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(AssignmentStatement.class, ast.getClass());
		AssignmentStatement assign = (AssignmentStatement) ast;
		assertEquals(IdentLValue.class, assign.getVar().getClass());
		assertEquals(BinaryExpression.class, assign.getE().getClass());
		//System.out.println(ast.toString());


	}
	
	@Test
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "image ashjdb ";
        //String input1 = "  (abc * 123 + 456 * true >= abc * 123 + 456,false / abc | 456 % screenheight != abc * 123 + 456) ";
		String dec1 = "image ashjdb ";
		String dec2 = "frame ashjdb ";
		String dec3 = "integer ashjdb ";
		String dec4 = "boolean nikhil ";
		Scanner scanner = new Scanner(dec4);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.dec();
		assertEquals(Dec.class, ast.getClass());
		Dec d = (Dec) ast;
		assertEquals(IDENT, d.getIdent().kind);
		assertEquals("nikhil", (d.getIdent()).getText());
		//System.out.println(ast.toString());
		//System.out.println(d.getIdent().getText());

	}
	
	
	@Test
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String param_dec1 = "url bsjdhbkj ";
		String param_dec2 = "file bsjd21hbkj ";
		String param_dec3 = "boolean bsjdhbkj ";
		String param_dec4 = "integer nikhil ";
		Scanner scanner = new Scanner(param_dec4);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.paramDec();
		assertEquals(ParamDec.class, ast.getClass());
		ParamDec d = (ParamDec) ast;
		assertEquals(IDENT, d.getIdent().kind);
		assertEquals("nikhil", (d.getIdent()).getText());
//		System.out.println(ast.toString());
//		System.out.println(d.getIdent().getText());

	}

	
	@Test
	public void Block() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{frame sdfjo12 image ewuiqhe boolean ffio sleep abc * 123 + 456 * true >= abc * 123 + 456; if(abc+123 >= acx){} frame sdfjo12 blur (3,5) -> hide |-> gray; as123 <- screenheight * ehiwqeh + ac23 > abc+123;} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.block();
		assertEquals(Block.class, ast.getClass());
		
	}
	
	@Test
	public void Block1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "{image ewuiqhe sleep true >= abc;} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.block();
		assertEquals(Block.class, ast.getClass());
		Block b = (Block) ast;
		ArrayList<Dec> decList = b.getDecs();
		ArrayList<Statement> stList = b.getStatements();
		assertEquals(Dec.class, decList.get(0).getClass());
		Statement st = stList.get(0);
		assertEquals(SleepStatement.class, st.getClass());
		System.out.println(st.toString());
	}
	
	@Test
	public void testStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "if(true >= abc){image ewuiqhe sleep true >= abc;} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(IfStatement.class, ast.getClass());
		IfStatement st1 = (IfStatement) ast;
		assertEquals(BinaryExpression.class, st1.getE().getClass());
		
		Block b= st1.getB();
		ArrayList<Dec> decList = b.getDecs();
		ArrayList<Statement> stList = b.getStatements();
		assertEquals(Dec.class, decList.get(0).getClass());
		Statement st = stList.get(0);
		assertEquals(SleepStatement.class, st.getClass());
		System.out.println(st.toString());
	}

	
	@Test
	public void testStatement4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "while(157 != abc){image ewuiqhe sleep true >= abc;} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.statement();
		assertEquals(WhileStatement.class, ast.getClass());
		WhileStatement st1 = (WhileStatement) ast;
		assertEquals(BinaryExpression.class, st1.getE().getClass());
		
		Block b= st1.getB();
		ArrayList<Dec> decList = b.getDecs();
		ArrayList<Statement> stList = b.getStatements();
		assertEquals(Dec.class, decList.get(0).getClass());
		Statement st = stList.get(0);
		assertEquals(SleepStatement.class, st.getClass());
		//System.out.println(st.toString());
	}
	
	
	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prog0 {} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(Program.class, ast.getClass());
		System.out.println(ast.toString());
	}
	
	@Test
	public void testProgram2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "prog1 boolean bsjdhbkj, file bsjd21hbkj {image ewuiqhe sleep true >= abc;} ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode ast = parser.parse();
		assertEquals(Program.class, ast.getClass());
		Program p = (Program) ast;
		ArrayList<ParamDec> paramDecList = p.getParams();
		assertEquals(ParamDec.class, paramDecList.get(0).getClass());
		assertEquals(ParamDec.class, paramDecList.get(1).getClass());
		Block b= p.getB();
		ArrayList<Dec> decList = b.getDecs();
		ArrayList<Statement> stList = b.getStatements();
		assertEquals(Dec.class, decList.get(0).getClass());
		Statement st = stList.get(0);
		assertEquals(SleepStatement.class, st.getClass());
		System.out.println(ast.toString());
	}
	
	
	
}

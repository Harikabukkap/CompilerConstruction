/**  Important to test the error cases in case the
 * AST is not being completely traversed.
 *
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.Statement;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;

public class TypeCheckVisitorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testOutOfScopeErr() throws Exception{
		String input = "pgm { integer int3 while(true){ integer int1 integer int2 int1 <- int1 / int2; } int2 <- 100; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testBinExp() throws Exception{
		String input = "pgm {\n image img1 image img2 \n img1 <- img1 - img2;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testBinExpErr1() throws Exception{
		String input = "pgm {\n integer int image img int <- int / img;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testBinExpErr2() throws Exception{
		String input = "pgm {\n integer int image img int <- img - int;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testMultiDeclarationErr() throws Exception{
		String input = "pgm {image img frame img}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testAssignBooleanLit() throws Exception{
		String input = "p {boolean bool \n bool <- false \n;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testAssignIntegerLit() throws Exception{
		String input = "pgm {integer int \n int <- 100;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testBinChain() throws Exception{
		String input = "pgm url u { frame frm image img u -> img -> frm -> hide(3+2) ;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void testBinChainErr() throws Exception{
		String input = "pgm {image img frame frm boolean bool  img -> bool -> frm;}";
		System.out.println(input);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testAssignBooleanLitErr() throws Exception{
		String input = "pgm { boolean bool bool <- 45;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testAssignImageErr() throws Exception{
		String input = "pgm {image img \n img <- a;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testAssignIntegerLitErr() throws Exception{
		String input = "pgm {integer int int <- true \n;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testUndeclaredVariableErr() throws Exception{
		String input = "pgm {i <- 3;}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testWhileStmtErr() throws Exception{
		String input = "pgm {integer int while(4-3){int <- int + 1;}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void testIfStmtErr() throws Exception{
		String input = "pgm {integer int if(1+5){int <- int - 1;}}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void test1() throws Exception{
		String input = "p integer a, integer b {image img1 image img2 if(img1 != img2) {image a a <- img1; } if(a != b) {boolean a a <- img1 != img2; }}";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void test2() throws Exception{
		String input = "tos url u,\n integer x\n{integer y image i u -> i; i -> height; frame f i -> scale (x) -> f;}";
		System.out.println(input);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void test3() throws Exception{
		String input = "prog {\n boolean x \n scale(1234) -> width; \n  integer y \n scale(y) -> scale(y + 10);}";
		System.out.println(input);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	/*@Test
	public void test4() throws Exception{
		String input = "prog  boolean y , file x {\n integer z \n scale(100) -> width; blur -> y; convolve -> blur -> gray |-> gray -> width;}";
		System.out.println(input);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}*/

	@Test
	public void test5() throws Exception{
		String input = "abc\n{integer x\nif(true){integer x}\n}";
		System.out.println(input);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void test6() throws Exception{
		String input = "prog1  file file1, integer itx, boolean b1{ integer ii1 boolean bi1 \n image IMAGE1 frame fram1 sleep itx+ii1; while (b1){if(bi1)\n{sleep ii1+itx*2;}}\nfile1->blur |->gray;fram1 ->yloc;\n IMAGE1->blur->scale (ii1+1)|-> gray;\nii1 <- 12345+54321;}";
		System.out.println(input);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}

	@Test
	public void test7() throws Exception{
		String input = "abc integer x, integer x {}";
		System.out.println(input);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		thrown.expect(TypeCheckVisitor.TypeCheckException.class);
		program.visit(v, null);
	}

	@Test
	public void test8() throws Exception{
		String input = "program { image first image last if(true) { integer first image second last->second->first;}}";
		System.out.println(input);
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);
	}




}

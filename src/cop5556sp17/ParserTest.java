package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}

	@Test
	public void testArg() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,5) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}
	
	@Test
	public void testProgramError() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "123 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
		
	}
	
	@Test
	public void testBlankProgramError() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = " ";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.parse();
		
	}
	
	@Test
	public void testEmptyBlock() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "{}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.block();
		
	}
	
	
	
	@Test
	public void testArg3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "123(";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.arg();
	}

	@Test
	public void testArgerror() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3,) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}


	@Test
	public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog0 {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "public boolean main2,url main {}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testProgram2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog02 file xyz , url link {if (x - y) { while (x + y) { convolve ( x , y , 5+6) |-> a -> hide ( a , b ) ; xyz <- 21*6+4 ; } } }";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
	}
	
	@Test
	public void testProgram3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog03 {boolean abc}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
	}
	
	@Test
	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "url abc";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.paramDec();
	}
	
	@Test
	public void testParamDecError() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "url2 abc";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.paramDec();
	}
	
	@Test
	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "{integer def sleep a<5;}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.block();
	}
	
	@Test
	public void testBlockError() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "integer def sleep a<5;}";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.block();
	}
	
	@Test
	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "frame abc";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.dec();
	}
	
	@Test
	public void testDecError() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "frames abc";
		Parser parser = new Parser(new Scanner(input).scan());
		thrown.expect(Parser.SyntaxException.class);
		parser.dec();
	}
	
	
	@Test
	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "blur -> width (x+y)";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.chain();
	}
	
	
	@Test
	public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "3 != 5 < x";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.expression();
	}

	@Test
	public void testTerm() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "3 + 5 - x | y";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.term();
	}
	
	@Test
	public void testElem() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "a * 5 & x / y";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.elem();
	}
	
	@Test
	public void testStatement() throws Exception
	{
		String input = "xyz url xyz , integer xyz {xyz -> blur |-> hide -> blur ( 120, 121, 122); }";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void testStatementChain() throws Exception
	{
		String input = "gray (a+b,c+d) -> show;";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		parser.statement();
	}
	
	@Test
	public void testStatementAssign() throws Exception
	{
		String input = "ident <- (a+b<c+d);";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		parser.statement();
	}
	
	@Test
	public void testStatementChainError() throws Exception
	{
		String input = "gray (a+b,c+d) -> show";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testStatementChainError2() throws Exception
	{
		String input = "gray -+ show";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	@Test
	public void testChainElem() throws Exception
	{
		String input = "xloc";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		parser.chainElem();
	}

	
	@Test
	public void testStatementError() throws Exception
	{
		String input = "if ((false)) {{integer this}}";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	
	@Test
	public void testStatementError2() throws Exception
	{
		String input = "while (true) { sleep (a<b) }";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	
	@Test
	public void testStatement3() throws Exception
	{
		String input = "this ->| thisname;";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	
	@Test
	public void testStatement4() throws Exception
	{
		String input = "while() {}";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}
	
	@Test
	public void testStatementWhile() throws Exception
	{
		String input = "while(a+b-c|d*e) {sleep (i<2);}";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		parser.statement();
	}
	
	@Test
	public void testStatementIf() throws Exception
	{
		String input = "if(a<b>c>=d!=e<=3) {sleep (i==2);}";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		parser.statement();
	}
	
	@Test
	public void testString() throws Exception
	{
		String input = "parser url File, file Boolean, integer URL, boolean Integer{integer a boolean b image c frame d sleep t>=t<=t>t<t!=t==t; while(34){sleep true;fal<-false;}if(screenheight<c){q<-screenweight%t+t-T|t*t%t/_&c;}/***if(0)/*/a -> blur -> gray->convolve->show(1)|->hide(st)|->move(false)->xloc->yloc(screenheight)|->width->height(false%true)|->scale;_<-$<$; }";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		parser.parse();
	}
	
	
	
}

package cop5556sp17;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;


public class ParserTest2 {

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
	public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "xyz123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}
	
	@Test
	public void testFactor2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "123";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.factor();
	}
	
	@Test
	public void testFactor3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "TRUE";
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
	public void testArg1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (abc,235,xy1) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}
	
	@Test
	public void testArg2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(ifEmpty)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}
	
	@Test
	public void testArg3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "(abc >= 567)";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(scanner);
		Parser parser = new Parser(scanner);
        parser.arg();
	}
	
	@Test
	public void testArg4() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "567(";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
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
	public void testArgerror1() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  (3, ,j) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		thrown.expect(Parser.SyntaxException.class);
		parser.arg();
	}

	@Test
	public void testArgerror2() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "  ) ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.arg();
	}
	
	@Test
	public void testArgerror3() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = " (  ) ";
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
		String input = "prog01 {sleep abc;}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testProgram2() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog02 url www , file f1 , boolean i , integer no {integer abc}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}

	@Test
	public void testProgram3() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog02 url www , file f1 {while (a | b) { if (a + b) { convolve ( a , b , 7+9) |-> abc -> hide ( n , m ) ; xyz <- 23*5+1 ; } } }";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.program();
	}

	@Test
	public void testProgram4() throws IllegalCharException, IllegalNumberException, SyntaxException{
		String input = "prog4 { if (23 + abc >  56 - dwedwd) {pr -> Hi;}}";
		Parser parser = new Parser(new Scanner(input).scan());
		parser.parse();
	}
	
	@Test
	public void testElem() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc * 123 / 11";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.elem();
	}
	
	@Test
	public void testExp() throws IllegalCharException, IllegalNumberException, SyntaxException {
		String input = "abc & TRUE | 4%2 != 2*67 + 100/5 ";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		parser.expression();
	}
	
	@Test
	public void customTest10a() throws Exception
	{
		String input = "sum <- sum2;";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		parser.statement();
	}

	//statement ::=  assign ;
	@Test
	public void customTest10b() throws Exception
	{
		String input = "sum -> sum2 |-> abc;";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		parser.statement();
	}
	
	//statement ::=  assign ;
	@Test
	public void customTest10c() throws Exception
	{
		String input = "abc url abc , integer abc {abc -> blur |-> hide -> blur ( 123, 123, 123); }";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void customTest10d() throws Exception
	{
		String input = "prog1 url www, boolean sup{integer a boolean b3 sleep abc & 23 | 4%2 != 100*6 + 125/5; hey |-> hi -> hello; z1 <- c2 * 42/4; while(something2!=true) { if(4+4 == b4){sleep 68%4;}}}";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		parser.parse();
	}
	
	@Test
	public void customTest240() throws Exception
	{
		String input = "while ((screen width)) { frame intcoiwn239999 }";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	//statement ::= ifStatement 
	@Test
	public void customTest251() throws Exception
	{
		String input = "if ((screenwidth) < (screenheight) { frame intcoiwn23 }";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		//System.out.println(scanner.tokens);
		Parser parser=new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	//statement ::=  assign ;
	@Test
	public void customTest261() throws Exception
	{
		String input = "sum <- sum2";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}

	//statement ::=   chain ; 
	@Test
	public void customTest271() throws Exception
	{
		String input = "name  name333;";
		Scanner scanner=new Scanner(input);
		scanner.scan();
		Parser parser=new Parser(scanner);
		thrown.expect(SyntaxException.class);
		parser.statement();
	}
	
	
	//@Rule

	//public ExpectedException thrown = ExpectedException.none();



	@Test

	public void testFactor10() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "abc";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.factor();

	}



	@Test

	public void testArg1111() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "  (3,5) ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		System.out.println(scanner);

		Parser parser = new Parser(scanner);

		parser.arg();

	}



	@Test

	public void testArgerror11() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "  (3,) ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.arg();

	}



	@Test

	public void testProgram10() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "prog0 {}";

		Parser parser = new Parser(new Scanner(input).scan());

		parser.parse();

	}



	@Test

	public void testFactor() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "()";

		Parser parser = new Parser(new Scanner(input).scan());

		thrown.expect(Parser.SyntaxException.class);

		parser.factor();



		String input1 = "screenheight";

		Parser parser1 = new Parser(new Scanner(input1).scan());

		parser1.factor();



		String input2 = "screenheight1234";

		Parser parser2 = new Parser(new Scanner(input2).scan());

		parser2.factor();



		String input3 = "screenheight 1234"; // Note that 1234 will not be

												// parsed with this method call

		Parser parser3 = new Parser(new Scanner(input3).scan());

		parser3.factor();

	}



	@Test

	public void testElem1() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "(abc*1234%5/def&true)";

		Parser parser = new Parser(new Scanner(input).scan());

		parser.elem();

		String input1 = "(abc*1234%5/def&&true)"; // strongOp not followed by

													// factor

		Parser parser1 = new Parser(new Scanner(input1).scan());

		thrown.expect(Parser.SyntaxException.class);

		parser1.elem();

	}



	@Test

	public void testTerm1() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "(abc*1234%5/def&true)";

		Parser parser = new Parser(new Scanner(input).scan());

		parser.term();

		String input1 = "abc*1234%5/def&true|false&21+121-something";

		Parser parser1 = new Parser(new Scanner(input1).scan());

		parser1.term();

		String input2 = "abc*1234%5/def&true|false&21+121-something/";

		Parser parser2 = new Parser(new Scanner(input2).scan());

		thrown.expect(Parser.SyntaxException.class);

		parser2.term();

		String input3 = "abc*1234%5/def&true|false&21+121-something and someMorething";

		Parser parser3 = new Parser(new Scanner(input3).scan());

		thrown.expect(Parser.SyntaxException.class);

		parser3.term();

	}



	@Test

	public void testExpression1() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "(something)<=somethingMore>somethingLess!=nothing==void";

		Parser parser = new Parser(new Scanner(input).scan());

		parser.expression();

	}



	@Test

	public void testArg12() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "";

		Parser parser = new Parser(new Scanner(input).scan());

		parser.arg();

		String input1 = "(something)<=somethingMore>somethingLess!=nothing==void";

		Parser parser1 = new Parser(new Scanner(input1).scan());

		parser1.arg();

		String input2 = "(something)<=somethingMore>somethingLess!=nothing==void, (), 1234, a*b, ,";

		Parser parser2 = new Parser(new Scanner(input2).scan());

		parser2.arg();

	}



	@Test

	public void testChainElem1() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "abc blur";// blur will be ignored

		Parser parser = new Parser(new Scanner(input).scan());

		parser.chainElem();

		String input1 = "blur";

		Parser parser1 = new Parser(new Scanner(input1).scan());

		parser1.chainElem();

		String input2 = "blur123";

		Parser parser2 = new Parser(new Scanner(input2).scan());

		parser2.chainElem();

	}



	@Test

	public void testIf11() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "if";

		Parser parser = new Parser(new Scanner(input).scan());

		thrown.expect(Parser.SyntaxException.class);

		parser.parse();

		// parser.ifStatement();

		String input1 = "if(){";

		Parser parser1 = new Parser(new Scanner(input1).scan());

		thrown.expect(Parser.SyntaxException.class);

		// parser1.ifStatement();

		parser1.parse();

		String input2 = "if(){}";

		Parser parser2 = new Parser(new Scanner(input2).scan());

		// parser2.ifStatement();

		parser2.parse();

		String input3 = "if(abc>=123){integer def while(1){def -> blur}}";

		Parser parser3 = new Parser(new Scanner(input3).scan());

		parser3.parse();

		// parser3.ifStatement();

		String input4 = "if(abc>=123){integer def while(1){def -> blur somethingToCauseError}}";

		Parser parser4 = new Parser(new Scanner(input4).scan());

		parser4.parse();

		thrown.expect(Parser.SyntaxException.class);

		// parser4.ifStatement();

	}



	// @Rule

	// public ExpectedException thrown = ExpectedException.none();



	@Test

	public void testFactor01() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "abc";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.factor();

	}



	@Test

	public void strongOp() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "%";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		// parser.strongOp();

	}



	@Test

	public void testIllegalElem() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "abc*teddy%";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.elem();

	}



	@Test

	public void testElem2() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "abc* teddy\r\n&bloody%turds/owl";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.elem();

	}



	@Test

	public void testIllegalTerm() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "abc* teddy\r\n&bloody%turds/owl+screenheight-screenwidth|toodles|";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.term();

	}



	@Test

	public void testTerm() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "abc* teddy\r\n&bloody%turds/owl-screenheight";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.term();

	}



	@Test

	public void testIllegalExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "screenheight-screenwidth!=";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.expression();

	}



	@Test

	public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "screenheight-screenwidth!=plato";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.expression();

	}



	@Test

	public void testArg23() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "  (3,5) ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.arg();

	}



	@Test

	public void testArgerror22() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "  (3,) ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.arg();

	}



	@Test

	public void testChainElem() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "blur  (3,5) ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.chainElem();

	}



	@Test

	public void testChainElemErr() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "blur  (3,) ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.chainElem();

	}



	@Test

	public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "blur  (3,5)->blur  (3,5)->blur  (3,5)";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.chain();

	}



	@Test

	public void testChainErr() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "blur  (3,5)->blur  (3,5)->->->blur  (3,) ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.chain();

	}



	@Test

	public void testAssign() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "blurcrazy<-screenheight-screenwidth!=plato";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		// parser.assign();

	}



	@Test

	public void testAssignErr() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "blurcrazy<-screenheight-screenwidth!=";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.parse();

		// parser.assign();

	}



	@Test

	public void testDec2() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "integer blue";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.dec();

	}



	@Test

	public void testDecErr() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "integer blur";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.dec();

	}



	@Test

	public void testParamDec2() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "url blue";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.paramDec();

	}



	@Test

	public void testParamDecErr() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "url blur";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.paramDec();

	}



	@Test

	public void testBlock() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "{integer blue sleep screenheight-screenwidth!=plato;}";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.block();

	}



	@Test

	public void testBlockErr() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "{integer blue sleep\n screenheight-screenwidth!=plato;";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.block();

	}



	@Test

	public void testWhileStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "while(true){integer blue sleep\n screenheight-screenwidth!=plato;}";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.statement();

	}



	@Test

	public void testWhileStatementErr() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "while(true)\r\n integer blue sleep\n screenheight-screenwidth!=plato;}";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.statement();

	}



	@Test

	public void testIfStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "if(screenheight-screenwidth!=plato){integer blue sleep\n screenheight-screenwidth!=plato;}";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.statement();

	}



	@Test

	public void testIfStatementErr() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "if(screenheight-screenwidth!=plato)\r\n {integer blue sleep\n -screenwidth!=plato;}";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.statement();

	}



	@Test

	public void testStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "if(screenheight-screenwidth!=plato)\r\n {integer blue sleep\n screenheight-screenwidth!=plato;blurcrazy<-screenheight-screenwidth!=plato;}";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.statement();

	}



	@Test

	public void testStatementErr() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "if(screenheight-screenwidth!=plato)\r\n {integer blue sleep\n screenheight-screenwidth!=;blurcrazy<-screenheight-screenwidth!=plato;} ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.statement();

	}



	@Test

	public void testProgram() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "toodles {if(screenheight-screenwidth!=plato)\r\n {integer blue sleep\n screenheight-screenwidth!=test;blurcrazy<-screenheight-screenwidth!=plato;}}";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.program();

	}



	@Test

	public void testProgramErr() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "terrible{if(screenheight-screenwidth!=plato)\r\n {integer blue sleep\n screenheight-screenwidth!=;blurcrazy<-screenheight-screenwidth!=plato;}} ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.program();

	}



	@Test

	public void testProgram00() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "prog0 {}";

		Parser parser = new Parser(new Scanner(input).scan());

		parser.parse();

	}



	@Test

	public void testIf1() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " if(ab>)  ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.parse();



		// parser.ifStatement();

	}



	@Test

	public void testIf2() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " if(8>  ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.parse();



		// parser.ifStatement();

	}



	@Test

	public void testBlock1() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " as}  ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.block();

	}



	@Test

	public void testBlock2() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " {as}  ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.block();

	}



	@Test

	public void testBlock3() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " {if(){}}  ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.block();

	}



	@Test

	public void testBlock4() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " {if(){}}  ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.block();

	}



	@Test

	public void testBlock5() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " {if(abc23 false){}}  ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.block();

	}



	@Test

	public void testBlock6() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " {if(abc23)} false)  ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.block();

	}



	@Test

	public void testBlock7() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " {if(abc23){}   ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.block();

	}



	@Test

	public void testBlock8() throws IllegalCharException, IllegalNumberException, SyntaxException {



		String input = " {int <- abc ; } ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		// thrown.expect(Parser.SyntaxException.class);

		parser.block();

	}



	@Test

	public void testStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " {int <- abc ; } ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.block();

	}



	@Test

	public void testCombine() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " abc{int <- abc ; }\nxyz<-true ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.program();

		// parser.assign();

	}



	@Test

	public void testParamDec() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " \nurl\nabc_1578 ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.paramDec();

	}



	@Test

	public void testDec() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " frame\n \n \n   _$56 ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.dec();

	}



	@Test

	public void testWhile() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " _$ {while(true){}} ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.program();

	}



	@Test

	public void testIf() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " _$ {if(true){}} ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.program();

	}



	@Test

	public void testExpression2() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = " $ {$ <- (a3<999999);} ";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		parser.program();

	}



	@Test

	public void blockFail() throws IllegalCharException, IllegalNumberException, SyntaxException {

		String input = "abc {";

		Scanner scanner = new Scanner(input);

		scanner.scan();

		Parser parser = new Parser(scanner);

		thrown.expect(Parser.SyntaxException.class);

		parser.program();



	}




	
	
	
}

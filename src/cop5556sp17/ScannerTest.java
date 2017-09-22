package cop5556sp17;

import static cop5556sp17.Scanner.Kind.SEMI;
import static cop5556sp17.Scanner.Kind.IDENT;
import static cop5556sp17.Scanner.Kind.INT_LIT;
import static cop5556sp17.Scanner.Kind.DIV;
import static cop5556sp17.Scanner.Kind.TIMES;
import static cop5556sp17.Scanner.Kind.NOT;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;

public class ScannerTest {

	@Rule
    public ExpectedException thrown = ExpectedException.none();

	// Method to validate token kind, position, text, length, line and posInLine
	Token nextTokenCheck(Scanner scanner, Scanner.Kind kind, int pos, String text, int line, int posInLine ) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(kind, token.kind);
		assertEquals(pos, token.pos);
		assertEquals(text, token.getText());
		assertEquals(text.length(), token.length);
		// Get the LinePos object of token to obtain its line number and posInLine
		LinePos lp = scanner.getLinePos(token);
		assertEquals(line,lp.line);
		assertEquals(posInLine, lp.posInLine);
		return token;
	}
	
	@Test
	public void testEmpty() throws IllegalCharException, IllegalNumberException {
		String input = "";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(input);
	}

	@Test
	public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
		//input string
		String input = ";;;";
		//create and initialize the scanner
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(input);
		//get the first token and check its kind, position, and contents
		Scanner.Token token = scanner.nextToken();
		assertEquals(SEMI, token.kind);
		assertEquals(0, token.pos);
		String text = SEMI.getText();
		assertEquals(text.length(), token.length);
		assertEquals(text, token.getText());
		//get the next token and check its kind, position, and contents
		Scanner.Token token1 = scanner.nextToken();
		assertEquals(SEMI, token1.kind);
		assertEquals(1, token1.pos);
		assertEquals(text.length(), token1.length);
		assertEquals(text, token1.getText());
		Scanner.Token token2 = scanner.nextToken();
		assertEquals(SEMI, token2.kind);
		assertEquals(2, token2.pos);
		assertEquals(text.length(), token2.length);
		assertEquals(text, token2.getText());
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token3 = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token3.kind);
	}
	
	
	/**
	 * This test illustrates how to check that the Scanner detects errors properly. 
	 * In this test, the input contains an int literal with a value that exceeds the range of an int.
	 * The scanner should detect this and throw and IllegalNumberException.
	 * 
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	@Test
	public void testIntOverflowError() throws IllegalCharException, IllegalNumberException{
		String input = "99999999999999999";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalNumberException.class);
		scanner.scan();		
		System.out.println(input);
	}
	
	//TODO  more tests
	@Test
	public void testBasicString() throws IllegalCharException, IllegalNumberException{
		String input = "abc!123/*^EWQ$*/true";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		System.out.println(input);
		//get tokens one by one and check its kind, position, text, length, line and posInLine
		nextTokenCheck(scanner, IDENT, 0, "abc",0,0);
		nextTokenCheck(scanner, NOT, 3,"!",0,3);
		nextTokenCheck(scanner, INT_LIT, 4,"123",0,4);
		nextTokenCheck(scanner, Scanner.Kind.KW_TRUE, 16, "true",0,16);
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token.kind);		
	}
	
	@Test
	public void testInvalidCharInOpenComment() throws IllegalCharException, IllegalNumberException{
		String input = "abc!123/*^EWQ$true";
		Scanner scanner = new Scanner(input);
		scanner.scan();
		System.out.println(input);
		//get tokens one by one and check its kind, position, text, length, line and posInLine
		nextTokenCheck(scanner, IDENT, 0, "abc",0,0);
		nextTokenCheck(scanner, NOT, 3,"!",0,3);
		nextTokenCheck(scanner, INT_LIT, 4,"123",0,4);
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token.kind);		
		
	}
	
	@Test
	public void testMultiNewLine() throws IllegalCharException, IllegalNumberException{
		String input = "this\n\n is a string\n literal\non multiple lines";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		System.out.println(input);
		//get tokens one by one and check its kind, position, text, length, line and posInLine
		nextTokenCheck(scanner, IDENT, 0, "this",0,0);
		nextTokenCheck(scanner, IDENT, 7,"is",2,1);
		nextTokenCheck(scanner, IDENT, 10,"a",2,4);
		nextTokenCheck(scanner, IDENT, 12, "string",2,6);
		nextTokenCheck(scanner, IDENT, 20,"literal",3,1);
		nextTokenCheck(scanner, IDENT, 28,"on",4,0);
		nextTokenCheck(scanner, IDENT, 31, "multiple",4,3);
		nextTokenCheck(scanner, IDENT, 40,"lines",4,12);
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token.kind);
				
	}
	
	@Test
	public void testTimesDiv() throws IllegalCharException, IllegalNumberException{
		String input = "/*string literal*/\non two*/lines";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		System.out.println(input);
		//get tokens one by one and check its kind, position, text, length, line and posInLine
		nextTokenCheck(scanner, IDENT, 19, "on",1,0);
		nextTokenCheck(scanner, IDENT, 22,"two",1,3);
		nextTokenCheck(scanner, TIMES, 25,"*",1,6);
		nextTokenCheck(scanner, DIV, 26, "/",1,7);
		nextTokenCheck(scanner, IDENT, 27,"lines",1,8);
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token.kind);		
	}
	
	@Test
	public void testIdentResOpSepNLine() throws IllegalCharException, IllegalNumberException{
		String input = "if123;if;screenheight!;boolean12+url|->frame->-\n\r";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		System.out.println(input);
		//get tokens one by one and check its kind, position, text, length, line and posInLine
		nextTokenCheck(scanner, IDENT, 0, "if123",0,0);
		nextTokenCheck(scanner, SEMI, 5,";",0,5);
		nextTokenCheck(scanner, Scanner.Kind.KW_IF, 6,"if",0,6);
		nextTokenCheck(scanner, SEMI, 8, ";",0,8);
		nextTokenCheck(scanner, Scanner.Kind.KW_SCREENHEIGHT, 9,"screenheight",0,9);
		nextTokenCheck(scanner, NOT, 21,"!",0,21);
		nextTokenCheck(scanner, SEMI, 22, ";",0,22);
		nextTokenCheck(scanner, IDENT, 23,"boolean12",0,23);
		nextTokenCheck(scanner, Scanner.Kind.PLUS, 32,"+",0,32);
		nextTokenCheck(scanner, Scanner.Kind.KW_URL, 33,"url",0,33);
		nextTokenCheck(scanner, Scanner.Kind.BARARROW, 36, "|->",0,36);
		nextTokenCheck(scanner, Scanner.Kind.KW_FRAME, 39,"frame",0,39);
		nextTokenCheck(scanner, Scanner.Kind.ARROW, 44,"->",0,44);
		nextTokenCheck(scanner, Scanner.Kind.MINUS, 46,"-",0,46);
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token.kind);		
	}
	
	@Test
	public void testIdentLit() throws IllegalCharException, IllegalNumberException{
		String input = "1234 name145 justname";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		System.out.println(input);
		//get tokens one by one and check its kind, position, text, length, line and posInLine
		nextTokenCheck(scanner, INT_LIT, 0, "1234",0,0);
		nextTokenCheck(scanner, IDENT, 5,"name145",0,5);
		nextTokenCheck(scanner, IDENT, 13,"justname",0,13);
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token.kind);		
	}
	
	@Test
	public void testNewLinesInComment() throws IllegalCharException, IllegalNumberException{
		String input = "/*1234 \nname145\n just\nname*/seethisline";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		System.out.println(input);
		//get tokens one by one and check its kind, position, text, length, line and posInLine
		nextTokenCheck(scanner, IDENT, 28, "seethisline",3,6);
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token.kind);			
	}
	
	@Test
	public void testIllegalChar() throws IllegalCharException, IllegalNumberException{
		String input = "tesIllegal char/n// =#abc";
		Scanner scanner = new Scanner(input);
		thrown.expect(IllegalCharException.class);
		thrown.expectMessage("=");
		scanner.scan();
		System.out.println(input);
	}
	
	@Test
	public void testIntVal() throws IllegalCharException, IllegalNumberException{
		String input="0678834 12 899";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		System.out.println(input);
		Scanner.Token t = scanner.nextToken();
		assertEquals(0,t.intVal());
		t = scanner.nextToken();
		assertEquals(678834,t.intVal());
		t = scanner.nextToken();
		assertEquals(12,t.intVal());
		t = scanner.nextToken();
		assertEquals(899,t.intVal());			
	}
	
	@Test
	public void testReservedKeywords() throws IllegalCharException, IllegalNumberException{
		String input = "move\n\n boolean xloc \r sleepscale";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		System.out.println(input);
		//get tokens one by one and check its kind, position, text, length, line and posInLine
		nextTokenCheck(scanner, Scanner.Kind.KW_MOVE, 0, "move",0,0);
		nextTokenCheck(scanner, Scanner.Kind.KW_BOOLEAN, 7,"boolean",2,1);
		nextTokenCheck(scanner, Scanner.Kind.KW_XLOC, 15,"xloc",2,9);
		nextTokenCheck(scanner, IDENT, 22,"sleepscale",2,16);
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token.kind);		
	}
	
	@Test
	public void testSepOp() throws IllegalCharException, IllegalNumberException{
		String input = ";,(}==!=%--><|-|&\n+<-";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		System.out.println(input);
		//get tokens one by one and check its kind, position, text, length, line and posInLine
		nextTokenCheck(scanner, SEMI, 0, ";",0,0);
		nextTokenCheck(scanner, Scanner.Kind.COMMA, 1,",",0,1);
		nextTokenCheck(scanner, Scanner.Kind.LPAREN, 2,"(",0,2);
		nextTokenCheck(scanner, Scanner.Kind.RBRACE, 3,"}",0,3);
		nextTokenCheck(scanner, Scanner.Kind.EQUAL, 4, "==",0,4);
		nextTokenCheck(scanner, Scanner.Kind.NOTEQUAL, 6,"!=",0,6);
		nextTokenCheck(scanner, Scanner.Kind.MOD, 8,"%",0,8);
		nextTokenCheck(scanner, Scanner.Kind.MINUS, 9,"-",0,9);
		nextTokenCheck(scanner, Scanner.Kind.ARROW, 10, "->",0,10);
		nextTokenCheck(scanner, Scanner.Kind.LT, 12,"<",0,12);
		nextTokenCheck(scanner, Scanner.Kind.OR, 13,"|",0,13);
		nextTokenCheck(scanner, Scanner.Kind.MINUS, 14,"-",0,14);
		nextTokenCheck(scanner, Scanner.Kind.OR, 15, "|",0,15);
		nextTokenCheck(scanner, Scanner.Kind.AND, 16,"&",0,16);
		nextTokenCheck(scanner, Scanner.Kind.PLUS, 18,"+",1,0);
		nextTokenCheck(scanner, Scanner.Kind.ASSIGN, 19,"<-",1,1);
		//check that the scanner has inserted an EOF token at the end
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF,token.kind);		
	}
	
	@Test
	public void testThis() throws IllegalCharException, IllegalNumberException {
		String input = "parser url File, file Boolean, integer URL, boolean Integer{integer a boolean b image c frame d sleep t>=t<=t>t<t!=t==t; while(34){sleep true;fal<-false;}if(screenheight<c){q<-screenweight%t+t-T|t*t%t/_&c;}/***if(0)/*/a -> blur -> gray->convolve->show(1)|->hide(st)|->move(false)->xloc->yloc(screenheight)|->width->height(false%true)|->scale;_<-$<$; }";
		Scanner scanner = new Scanner(input);
		scanner.scan();	
		System.out.println(input);
		
		
	}
	
}

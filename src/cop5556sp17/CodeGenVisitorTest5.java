
package cop5556sp17;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Program;

public class CodeGenVisitorTest5 {

    static final boolean doPrint = false;
    static void show(Object s) {
        if (doPrint) {
            System.out.println(s);
        }
    }

    boolean devel = false;
    boolean grade = true;

    private final ByteArrayOutputStream outputContent = new ByteArrayOutputStream();

    @Test
    public void emptyProg() throws Exception {
        //scan, parse, and type check the program
        String progname = "emptyProg";
        String input = progname + "  {}";
        String[] args = new String[0];

        testEasy(input,args,"");
    }

    @Test
    public void identExprBooleanLocal() throws Exception {
        String progname = "identExprBooleanLocal";
        String input = progname + " {boolean i boolean j i<-false; j <- i;}";

        String[] args = new String[0];

        testEasy(input, args, "falsefalse");
    }

    @Test
    public void identExprBooleanParam() throws Exception {
        String progname = "identExprBooleanParam";
        String input = progname + " boolean i { boolean j j <- i;}";

        String[] args = new String[] {"false"};

        testEasy(input, args, "false");
    }

    @Test
    public void exprComp() throws Exception {
        String progname = "exprComp";
        String input = progname + " { integer a a <- 1 - 3 * (2 + 1 -6) +2 + 5/2;} ";

        String[] args = new String[0];

        testEasy(input, args, "14");
    }

    @Test
    public void compProg0() throws Exception {
        String progname = "compProg0";
        String input = progname + " { integer a0 a0<-0;if(a0 == 0){integer a00 integer b00 integer c00 integer d00 integer e00 e00 <- 5; d00 <- 4; c00 <- 3; b00 <- 2; a00 <- 1; if(a00 == 1){integer a01 integer b01 integer c01 integer d01 integer e01 e01 <- 55; d01 <- 44; c01 <- 33; b01 <- 22; a01 <- 11; }}} ";

        String[] args = new String[0];

        testEasy(input, args, "0543215544332211");
    }

    @Test
    public void compProg1() throws Exception {
        String progname = "compProg1";
        String input = progname + " integer a, integer b, integer c, boolean bool0 { a <- 4; b <- 5; boolean boolA boolean boolB boolA <- true; boolB <- false; if(boolA == true) {boolean a a <- boolA; bool0 <- false;while(a != boolB){integer d integer e c <- 3 + 5; d <- 10 - 1; c <- c * d; e <- d / 3; a <- boolB;if(c > d) { c <- d; if(c <= d) { boolA <- false; } if(boolA < boolB) { c <- 0; }}} } if(c >= 1) { /*boolB <- bool0 | true;*/} a <- 7;}";

        String[] args = new String[] {"1", "2", "3", "true"};

        testEasy(input, args, "45truefalsetruefalse89723false9false7");
    }

    @Test
    public void compProg2() throws Exception {
        String progname = "compProg2";
        String input = progname + " integer x, integer y, integer z, boolean bool_1, boolean bool_2 { \nx <- 100; \ny <- x / 3 * 2; \nz <- y; \nbool_1 <- false; \nbool_2 <- true; \ninteger y \ny <- z + 20; \nz <- y; \nif(bool_2){ \nboolean bool_1 \nbool_1 <- bool_2; \n} \nif(bool_1) { \ninteger err \nerr <- 2333; \n} \ninteger pass_token \npass_token <- 0; \nwhile(pass_token != 4) { \ninteger local_1 \ninteger local_2 \nlocal_1 <- 45; \nlocal_2 <- 46; \nif(local_1 != local_2) {pass_token <- pass_token + 1;} \nif(local_1 == local_2) {pass_token <- pass_token + 1;} \nif(local_1 > local_2) {pass_token <- pass_token + 1;} \nif(local_1 >= 45) {pass_token <- pass_token + 1;} \nif(local_1 < local_2) {pass_token <- pass_token + 1;} \nif(46 <= local_2) {pass_token <- pass_token + 1;} \nif((local_1 > local_2)) {pass_token <- pass_token + 1;} \n} \n} ";
        System.out.println(input);
        String[] args = new String[] {"1", "2", "3", "true", "false"};

        testEasy(input, args, "1006666falsetrue8686true045461234");
    }

    @Test
    public void whileifwhileStatement0() throws Exception {
        String progname = "whileifwhileStatement0";
        String input = progname + " {\ninteger i \ninteger j \ninteger t \ni <-10; \nj <-1; \nt <-2; \nwhile (i > 0) {\ninteger k \nk <-i/j; \nif (k > 1) {\nwhile (t > 0) {t <- t-1;} \nj <- j+1;} \ni<-i-1;} \ni<-t;}";

        String[] args = new String[0];

        testEasy(input, args, "1012101029438247161514130201000");
    }

    @Test
    public void whileifwhileStatement1() throws Exception {
        String progname = "whileifwhileStatement1";
        String input = progname + " {\ninteger i \ninteger j \ninteger t \ni <-10; \nj <-1; \nt <-2; \nwhile (i > 0) {\ninteger k \nk <-i/j; \nif (k > 1) {\ninteger t \nt <-j; \nwhile (t > 0) {t <- t-1;} \nj <- j+1;} \ni<-i-1;} \ni<-t;}";

        String[] args = new String[0];

        testEasy(input, args, "10121010294210382321047161514130201002");
    }

    @Test
    public void assignParamNLocal() throws Exception {
        String progname = "assignParamNLocal";
        String input = progname + " integer int_foo, boolean bool_bar {int_foo <- 42;\n bool_bar <- false;integer local_foo0 \n local_foo0 <- 5; boolean local_bool0 \n local_bool0 <- true;}";

        String[] args = new String[] {"1", "true"};

        testEasy(input, args, "42false5true");
    }

    @Test
    public void ifwhileStatement0() throws Exception {
        String progname = "ifwhileStatement0";
        String input = progname + " {\ninteger i \ninteger j \ni <-10; \nj <-1; \nif (i > 0) {\ninteger k \nk <-i/j; \nwhile (k > 1) {j <- j+1; \nk <-i/j;} \n} \ni<-j;}";

        String[] args = new String[0];

        testEasy(input, args, "1011025334252616");
    }

    @Test
    public void booleanComp1() throws Exception {
        String progname = "booleanComp1";
        String input = progname + " { boolean a a<- true == false;a <- 4< 5;a<- 4<=5;a<- 4 == 5;a<- 5 == 5;a<- 5 >= 4;a<- 5>=5;a<- 6> 5;a<- 4 != 5;a<- 4 < 5;a<- 4 <= 4;} ";

        String[] args = new String[0];

        testEasy(input, args, "falsetruetruefalsetruetruetruetruetruetruetrue");
    }

    @Test
    public void booleanComp2() throws Exception {
        String progname = "booleanComp2";
        String input = progname + " { boolean a boolean b boolean c a<-true;b<-false; c<-a<b;c<-a<=b;c<-a>b;c<-a>=b;c<-a==b;c<-a!=b;a<-false; b<-true;c<-a<b;c<-a<=b;c<-a>b;c<-a>=b;c<-a==b;c<-a!=b;a<-true; b<-true;c<-a<b;c<-a<=b;c<-a>b;c<-a>=b;c<-a==b;c<-a!=b;a<-false; b<-false;c<-a<b;c<-a<=b;c<-a>b;c<-a>=b;c<-a==b;c<-a!=b;}";

        String[] args = new String[0];

        testEasy(input, args, "truefalsefalsefalsetruetruefalsetruefalsetruetruetruefalsefalsefalsetruetruetruefalsetruefalsetruetruefalsefalsefalsefalsetruefalsetruetruefalse");
    }

    @Test
    public void whileStatement0() throws Exception {
        String progname = "whileStatement0";
        String input = progname + " {\ninteger i \ninteger j \ni <-4; \nwhile (i > 0) {\nj <- i;\ni<-i-1;} \ni<-9;}";

        String[] args = new String[0];

        testEasy(input, args, "4433221109");
    }

    @Test
    public void whileStatement1() throws Exception {
        String progname = "whileStatement1";
        String input = progname + " integer y { integer x\tx <- 6;\ty <- x + 1; \twhile(x >= 2) { \t\tx <- x - 1;\t}}";

        String[] args = new String[1];
        args[0] = "1";

        testEasy(input, args, "6754321");
    }

    @Test
    public void whileStatement2() throws Exception {
        String progname = "whileStatement2";
        String input = progname + " {\ninteger i \ninteger j \ni <-3; \nwhile (i > 0) {\ninteger j \nj <-i*2; \nwhile (j > i) {\nj<-j-1;} \ni<-i-1;} \ni<-0;}";

        String[] args = new String[0];

        testEasy(input, args, "36543243212100");
    }

    @Test
    public void identExprBooleanLocal1() throws Exception {
        String progname = "identExprBooleanLocal1";
        String input = progname + " {boolean i boolean j i<-true; j <- i;}";

        String[] args = new String[0];

        testEasy(input, args, "truetrue");
    }

    @Test
    public void ifStatement0() throws Exception {
        String progname = "ifStatement0";
        String input = progname + " {integer i \ninteger j \ni <-55; \nif (i == 55) {j <- 3;} \nif (i != 55){ j <- 1;}\n}";

        String[] args = new String[0];

        testEasy(input, args, "553");
    }

    @Test
    public void ifStatement1() throws Exception {
        String progname = "ifStatement1";
        String input = progname + " {\ninteger i \ninteger j \ni <-56; \nif (i == 55) {j <- 3;} \nif (i != 55){ j <- 1;}\n}";

        String[] args = new String[0];

        testEasy(input, args, "561");
    }

    @Test
    public void ifStatement2() throws Exception {
        String progname = "ifStatement2";
        String input = progname + " {\ninteger i \ninteger j \ni <-10; \nj <-0; \nif (i > 5) {j <- j+1; \nif (i > 7){ j <- j + 1; \nif (i > 8){ j <- j + 1;}\n}\n}\n}";

        String[] args = new String[0];

        testEasy(input, args, "100123");
    }

    @Test
    public void ifStatement3() throws Exception {
        String progname = "ifStatement3";
        String input = progname + " {\ninteger i \ninteger j \ni <-10; \nj <-0; \nif (i > 5) {j <- j+1; \nif (i > 7){ \ninteger i \ni <-7; j <- j + 1; \nif (i > 8){ j <- j + 1;}\n}\n}\n}";

        String[] args = new String[0];

        testEasy(input, args, "100172");
    }

    @Test
    public void ifStatement4() throws Exception {
        String progname = "ifStatement4";
        String input = progname + " {integer local_int0\ninteger local_int1\nlocal_int0 <- 42;local_int1 <- 43;if(local_int0 == local_int1){integer local_int11 \n local_int11 <- 44;} if(local_int0 != local_int1){integer local_int22 \n local_int22 <- 45;}if(local_int0 != local_int1){integer local_int33 \n local_int33 <- 46;integer local_int44 \n local_int44 <- 47;}}";

        String[] args = new String[0];

        testEasy(input, args, "4243454647");
    }

    @Test
    public void ifwhileifStatement0() throws Exception {
        String progname = "ifwhileifStatement0";
        String input = progname + " {\ninteger i \ninteger j \ninteger k \nboolean b \ni <-10; \nj <-1; \nb <-true; \nif (b) {\nwhile (i > 0) {\nk <-i/j; \nif (k > 1) {j <- j+1;} \ni<-i-1;} \ni<-0;}\n}";

        String[] args = new String[0];

        testEasy(input, args, "101true1029438247161514130201000");
    }

    @Test
    public void ifwhileifStatement1() throws Exception {
        String progname = "ifwhileifStatement1";
        String input = progname + " {\ninteger i \ninteger j \nboolean b \ni <-10; \nj <-1; \nb <-true; \nif (b) {\nwhile (i > 0) {\ninteger k \nk <-i/j; \nif (k > 1) {j <- j+1;} \ni<-i-1;} \ni<-0;}\n}";

        String[] args = new String[0];

        testEasy(input, args, "101true1029438247161514130201000");
    }

    @Test
    public void add() throws Exception {
        String progname = "add";
        String input = progname + " {integer i \ninteger j \ni <-55; \nj <- 44; \nj <- i+j;\n}";

        String[] args = new String[0];

        testEasy(input, args, "554499");
    }

    @Test
    public void div() throws Exception {
        String progname = "div";
        String input = progname + " {integer i \ninteger j \ni <-33; \nj <- 3; \nj <- i/j;\n}";

        String[] args = new String[0];

        testEasy(input, args, "33311");
    }

    @Test
    public void times() throws Exception {
        String progname = "times";
        String input = progname + " {integer i \ninteger j \ni <-11; \nj <- 3; \nj <- i*j;\n}";

        String[] args = new String[0];

        testEasy(input, args, "11333");
    }

    @Test
    public void identExprLocal() throws Exception {
        String progname = "identExprLocal";
        String input = progname + " {integer i integer j i<-55; j <- i;}";

        String[] args = new String[0];

        testEasy(input, args, "5555");
    }

    @Test
    public void identExprParam() throws Exception {
        String progname = "identExprParam";
        String input = progname + " integer i { integer j j <- i;}";

        String[] args = new String[] {"99"};

        testEasy(input, args, "99");
    }

    @Test
    public void whileifStatement0() throws Exception {
        String progname = "whileifStatement0";
        String input = progname + " {\ninteger i \ninteger j \ni <-10; \nj <-1; \nwhile (i > 0) {\ninteger k \nk <-i/j; \nif (k > 1) {j <- j+1;} \ni<-i-1;} \ni<-0;}";

        String[] args = new String[0];

        testEasy(input, args, "1011029438247161514130201000");
    }

    @Test
    public void assignLocal() throws Exception {
        String progname = "assignLocal";
        String input = progname + " {integer i boolean b i<-33; b<-false;}";

        String[] args = new String[0];

        testEasy(input, args, "33false");
    }

    @Test
    public void assignParam() throws Exception {
        String progname = "assignParam";
        String input = progname + " integer i, boolean b {i<-33; b<-false;}";

        String[] args = new String[] {"1", "true"};

        testEasy(input, args, "33false");
    }

    private void testEasy(String input, String []args, String expectedContent) throws Exception {
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
        show(program);

        //generate code
        CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
        byte[] bytecode = (byte[]) program.visit(cv, null);

        //output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

        String name = ((Program) program).getName();

        // directly execute bytecode
        Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
        instance.run();

        assertEquals(expectedContent, PLPRuntimeLog.getString());
    }

    //	@Before
    public void setUpOutputStream() {
        System.setOut(new PrintStream(outputContent));
    }

    //	@After
    public void cleanUpOutputStream() {
        System.setOut(null);
    }

    @Before
    public void initLog() {
        if (devel || grade) PLPRuntimeLog.initLog();
    }

    @After
    public void printLog(){
        System.out.println(PLPRuntimeLog.getString());
    }
}

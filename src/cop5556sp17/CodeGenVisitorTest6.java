package cop5556sp17;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Program;

public class CodeGenVisitorTest6 {

	static final boolean doPrint = true;

	static void show(Object s) {
		if (doPrint) {
			System.out.println(s);
		}
	}

	boolean devel = false;
	boolean grade = false;

	@Before
	public void initLog() {

		if (devel || grade)
			PLPRuntimeLog.initLog();

	}

	@After
	public void printLog() {

		System.out.println(PLPRuntimeLog.getString());

	}

	@Test
	public void emptyProg() throws Exception {
		// scan, parse, and type check the program

		String progname = "LQTest ";
		String input = progname
				+ " integer int1,integer int2,boolean bool1,boolean bool2,file file1,file file2,file outputFile1,url url1,url url2{\n"
				+ "integer int3\n" + "integer int4\n" + "integer int5\n" + "integer int6\n" + "boolean bool3\n"
				+ "boolean bool4\n" + "image image1\n" + "image image2\n" + "image image3\n" + "image image4\n"
				+ "image image5\n" + "frame frame1\n" + "frame frame2\n" + "frame frame3\n" + "frame frame4\n"
				+ "frame frame5\n"

				+ "int3 <- screenwidth;\n" + "int4 <- screenheight;\n"

				+ "url1 -> image2;\n" + "url2 -> image3;\n" + "image1 <- image2 + image3;\n"

				+ "image2 -> frame1;\n" + "frame1 -> move(200, 50);\n" + "frame1 -> show;\n" + "sleep 500;\n"
				+ "image3 -> frame2 -> move(300, 100) -> show;\n" + "sleep 500;\n"
				+ "image1 -> frame3 -> move(400, 150) -> show;\n" + "sleep 500;\n" + "int3 <- int1 + 50;\n"
				+ "int4 <- int2 + 50;\n" + "frame3 -> move(int3, int4);\n" + "sleep 500;\n" + "frame3 -> hide;\n"

				+ "image3 -> image4;\n" + "image5 <- image3;\n" + "image3 -> blur -> blur -> blur;\n"
				+ "image4 -> frame4;\n" + "frame4 -> xloc -> int5;\n" + "frame4 -> yloc -> int6;\n"
				+ "frame4 -> show;\n" + "image5 -> frame5 -> show;\n"

				+ "if (image3 == image4) {\n" + "image i3scale\n" + "image3 -> scale(2) -> i3scale -> frame3 -> show;\n"
				+ "sleep 500;\n" + "frame3 -> hide;\n" + "image itemp\n" + "url1 -> itemp;\n"
				+ "itemp <- itemp + image3;\n" + "itemp -> outputFile1;\n" + "integer w\n" + "integer h\n"
				+ "image4 -> width -> w;\n" + "image4 -> height -> h;\n" + "w <- w;\n" + "h <- h;\n" + "integer w2\n"
				+ "integer h2\n" + "i3scale -> width -> w2;\n" + "i3scale -> height -> h2;\n" + "w2 <- w2;\n"
				+ "h2 <- h2;\n" + "}\n"

				+ "file1 -> image1 -> gray -> frame1 -> show;\n" + "file2 -> image2 -> convolve -> frame2 -> show;\n"
				+ "url1 -> image1;\n" + "image1 <- image1 % 150;\n" + "image1 -> frame5 -> show;\n" + "if (true) {\n"
				+ "integer x\n" + "integer y\n" + "integer xi\n" + "integer yi\n" + "xi <- 100;\n" + "yi <- 50;\n"
				+ "x <- xi;\n" + "y <- yi;\n" + "file1 -> image1;\n" + "file2 -> image2;\n" + "url1 -> image3;\n"
				+ "url2 -> image4;\n" + "int3 <- 15;\n" + "image itemp\n" + "itemp <- image1;\n"
				+ "while (int3 > 0) {\n" + "frame ftemp\n"

				+ "if (int3 % 5 == 0) {\n" + "itemp <- itemp + image2;\n" + "}\n" + "if (int3 % 5 == 1) {\n"
				+ "itemp <- itemp - image2;\n" + "}\n" + "if (int3 % 5 == 2) {\n" + "itemp <- itemp + image4;\n" + "}\n"
				+ "if (int3 % 5 == 3) {\n" + "itemp <- itemp - image4;\n" + "}\n" + "if (int3 % 5 == 4) {\n"
				+ "itemp <- image1;\n" + "}\n"

				+ "itemp -> ftemp -> move(x, y) -> show;\n" + "sleep 300;\n" + "ftemp -> hide;\n"

				+ "x <- x + 100;\n" + "if (x > 500) {\n" + "x <- xi;\n" + "}\n" + "y <- y + 50;\n" + "if (y > 300) {\n"
				+ "y <- yi;\n" + "}\n" + "int3 <- int3 - 1;\n" + "}\n" + "}\n"

				+ "file1 -> image1 -> frame1;\n" + "file2 -> image2 -> frame2;\n" + "url1 -> image3 -> frame3;\n"
				+ "url2 -> image4 -> frame4;\n"

				+ "integer x\n" + "x <- 230;\n" + "integer y\n" + "y <- 170;\n"

				+ "integer loop\n" + "loop <- 20;\n" + "while (loop > 0) {\n" + "if (loop % 4 == 0) {\n"
				+ "image1 -> frame1 -> move(x, y) -> show;\n" + "image1 <- image1 - image3;\n"

				+ "}\n" + "if (loop % 4 == 1) {\n" + "image2 -> frame2 -> move(x, y) -> show;\n"
				+ "image2 <- image2 + image4;\n"

				+ "}\n" + "if (loop % 4 == 2) {\n" + "image3 -> frame3 -> move(x, y) -> show;\n"
				+ "image3 <- image1 * 3;\n" + "file1 -> image1;\n" + "}\n" + "if (loop % 4 == 3) {\n"
				+ "image4 -> frame4 -> move(x, y) -> show;\n" + "image4 <- image2 / 2;\n" + "file2 -> image2;\n" + "}\n"
				+ "sleep (x);\n" + "x <- x * 17 % 1100;\n" + "y <- y * 31 % 370;\n" + "loop <- loop - 1;\n" + "}\n"

				+ "}\n";

		Scanner scanner = new Scanner(input);
		scanner.scan();
		Parser parser = new Parser(scanner);
		ASTNode program = parser.parse();
		TypeCheckVisitor v = new TypeCheckVisitor();
		program.visit(v, null);

		show(program);

		// generate code
		CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
		byte[] bytecode = (byte[]) program.visit(cv, null);

		// output the generated bytecode
		CodeGenUtils.dumpBytecode(bytecode);

		// write byte code to file
		String name = ((Program) program).getName();
		String classFileName = "bin/" + name + ".class";
		OutputStream output = new FileOutputStream(classFileName);
		output.write(bytecode);
		output.close();
		System.out.println("wrote classfile to " + classFileName);

		// directly execute bytecode
		String[] args = new String[9]; // create command line argument array to
										// initialize params, none in this case
		args[0] = "50";
		args[1] = "100";
		args[2] = "true";
		args[3] = "false";
		args[4] = "Images/image1.jpg";
		args[5] = "Images/image2.jpg";
		args[6] = "Images/output1.jpg";
		args[7] = "https://s3.amazonaws.com/glocal-files/image/bi+(65).jpg";
		args[8] = "https://s3.amazonaws.com/glocal-files/image/bi+(100).jpg";

		// args[0] = "https://s3.amazonaws.com/glocal-files/image/bi+(65).jpg";
		// args[1] = "false";

		Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
		instance.run();
	}

}
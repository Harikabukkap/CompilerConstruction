package cop5556sp17;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;

	}

	int slot = 1;

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	int currDecSlot = 0;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		int i =0;
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params) {
			dec.visit(this, mv);
		}
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
		//TODO  visit the local variables
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method


		cw.visitEnd();//end of class

		//generate classfile and return it
		return cw.toByteArray();
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {

		Expression e = assignStatement.getE();
		IdentLValue ilv = assignStatement.getVar();

		e.visit(this, arg);

		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + ilv.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, e.getType());

		ilv.visit(this, arg);

		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {

		// visit left Chain
		Token arrow = binaryChain.getArrow();
		Chain c = binaryChain.getE0();
		c.visit(this,"left");



			switch(c.getType()) {
			case URL:
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromURL",
						PLPRuntimeImageIO.readFromURLSig, false);
				break;
			case FILE:
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "readFromFile",
						PLPRuntimeImageIO.readFromFileDesc, false);
				break;
			default:
				break;
			}

		if (arrow.isKind(Kind.BARARROW))
				mv.visitInsn(DUP);

		// visit right ChainElem
		ChainElem ce = binaryChain.getE1();

		if (arrow.isKind(Kind.BARARROW)) {
			ce.visit(this,"bararrow");
		} else {
			ce.visit(this, "right");
		}

		try {

			if (ce instanceof IdentChain) {

				IdentChain identChain = (IdentChain) ce;
				TypeName type = identChain.getType();
				Dec decl = identChain.getDec();
				String text = identChain.getDec().getIdent().getText();

				// if a class variable
				if ((decl instanceof ParamDec)) {

					// if an integer
					if (type.isType(TypeName.INTEGER)) {
						mv.visitVarInsn(ALOAD, 0);
						mv.visitFieldInsn(GETFIELD, className,text,type.getJVMTypeDesc());
					}
				}
				// if a local variable
				else {
					if (type.isType(TypeName.INTEGER)) {
						mv.visitVarInsn(ILOAD, decl.getSlot());
					} else {
						mv.visitVarInsn(ALOAD, decl.getSlot());
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		//TODO  Implement this

		// Visit the expressions on both sides of binary expression operand

		Expression e0 = binaryExpression.getE0();
		Expression e1 = binaryExpression.getE1();

		if (binaryExpression.getOp().isKind(Kind.AND, Kind.OR)) {

			Label label1 = new Label();
			Label label2 = new Label();

			if(binaryExpression.getOp().isKind(Kind.OR)){

				e0.visit(this, arg);
				mv.visitJumpInsn(IFNE, label1);

				e1.visit(this, arg);
				mv.visitJumpInsn(IFNE, label1);

				mv.visitInsn(ICONST_0);

				mv.visitJumpInsn(GOTO, label2);

				mv.visitLabel(label1);
				mv.visitInsn(ICONST_1);

				mv.visitLabel(label2);
			}

			else if( binaryExpression.getOp().isKind(Kind.AND)) {

				e0.visit(this, arg);
				mv.visitJumpInsn(IFNE, label1);

				e1.visit(this, arg);
				mv.visitJumpInsn(IFNE, label1);
				mv.visitInsn(ICONST_0);

				mv.visitJumpInsn(GOTO, label2);

				mv.visitLabel(label1);
				mv.visitInsn(ICONST_1);

				mv.visitLabel(label2);
			}
			else{

			}
		}
		else {

			// Declare a hashmap to store the op, function to execute pairs
			HashMap <String, Integer> binExpOpFun = new HashMap<>();
			binExpOpFun.put(Kind.EQUAL.getText(),IF_ICMPEQ);
			binExpOpFun.put(Kind.NOTEQUAL.getText(),IF_ICMPNE);
			binExpOpFun.put(Kind.LT.getText(),IF_ICMPLT);
			binExpOpFun.put(Kind.GT.getText(),IF_ICMPGT);
			binExpOpFun.put(Kind.GE.getText(),IF_ICMPGE);
			binExpOpFun.put(Kind.LE.getText(),IF_ICMPLE);
			binExpOpFun.put(Kind.PLUS.getText(),IADD);
			binExpOpFun.put(Kind.MINUS.getText(),ISUB);
			binExpOpFun.put(Kind.AND.getText(),IAND);
			binExpOpFun.put(Kind.OR.getText(),IOR);
			binExpOpFun.put(Kind.TIMES.getText(),IMUL);
			binExpOpFun.put(Kind.DIV.getText(),IDIV);
			binExpOpFun.put(Kind.MOD.getText(),IREM);

			// Declare a hashmap to store the op, function to execute pairs
			HashMap <String, Integer> expOpFun = new HashMap<>();
			expOpFun.put(Kind.EQUAL.getText(),IF_ACMPEQ);
			expOpFun.put(Kind.NOTEQUAL.getText(),IF_ACMPNE);

			// Declare label variables
			Label trueCondi = new Label();
			Label endLabel = new Label();

			e0.visit(this, arg);
			e1.visit(this, arg);

			Kind op = binaryExpression.getOp().kind;

			switch(op) {

			case PLUS:

				if (e0.getType().isType(TypeName.INTEGER)&& e1.getType().isType(TypeName.INTEGER)){

					mv.visitInsn(binExpOpFun.get(op.getText()));
					mv.visitJumpInsn(GOTO, endLabel);
				}
				else {
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName,
							"add", PLPRuntimeImageOps.addSig, false);
					mv.visitJumpInsn(GOTO, endLabel);
				}
				break;

			case MINUS:
				if (e0.getType().isType(TypeName.INTEGER)&& e1.getType().isType(TypeName.INTEGER)){
					mv.visitInsn(binExpOpFun.get(op.getText()));
					mv.visitJumpInsn(GOTO, endLabel);
				}
				else {
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName,
							"sub", PLPRuntimeImageOps.subSig, false);
					mv.visitJumpInsn(GOTO, endLabel);
				}
				break;

			case TIMES:
				if (e0.getType().isType(TypeName.INTEGER)&& e1.getType().isType(TypeName.INTEGER)){
					mv.visitInsn(binExpOpFun.get(op.getText()));
					mv.visitJumpInsn(GOTO, endLabel);
				}
				else {

					if ((e0.getType().isType(TypeName.IMAGE) && e1.getType().isType(TypeName.INTEGER))){
						mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName,
								"mul", PLPRuntimeImageOps.mulSig, false);
						mv.visitJumpInsn(GOTO, endLabel);
					}
					else {
						mv.visitInsn(SWAP);
						mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName,
								"mul", PLPRuntimeImageOps.mulSig, false);
						mv.visitJumpInsn(GOTO, endLabel);
					}
				}
				break;

			case DIV:
				if (e0.getType().isType(TypeName.INTEGER)&& e1.getType().isType(TypeName.INTEGER)){
					mv.visitInsn(binExpOpFun.get(op.getText()));
					mv.visitJumpInsn(GOTO, endLabel);
				}
				else {
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName,
							"div", PLPRuntimeImageOps.divSig, false);
					mv.visitJumpInsn(GOTO, endLabel);
				}
				break;
			case MOD:
				if (e0.getType().isType(TypeName.INTEGER)&& e1.getType().isType(TypeName.INTEGER)){
					mv.visitInsn(binExpOpFun.get(op.getText()));
					mv.visitJumpInsn(GOTO, endLabel);
				}
				else {
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName,
							"mod", PLPRuntimeImageOps.modSig, false);
					mv.visitJumpInsn(GOTO, endLabel);
				}
				break;

			case EQUAL:
			case NOTEQUAL:

				if (e0.getType().isType(TypeName.INTEGER) | e0.getType().isType(TypeName.BOOLEAN)) {
					mv.visitJumpInsn(binExpOpFun.get(op.getText()), trueCondi);
					mv.visitLdcInsn(false);
					mv.visitJumpInsn(GOTO, endLabel);
				}
				else {
					mv.visitJumpInsn(expOpFun.get(op.getText()), trueCondi);
					mv.visitLdcInsn(false);
					mv.visitJumpInsn(GOTO, endLabel);

				}
				break;

			case LT:
			case GT:
			case LE:
			case GE:
				mv.visitJumpInsn(binExpOpFun.get(op.getText()), trueCondi);
				mv.visitLdcInsn(false);
				mv.visitJumpInsn(GOTO, endLabel);

				break;

			default:
				break;

			}

			mv.visitLabel(trueCondi);
			mv.visitLdcInsn(true);
			mv.visitLabel(endLabel);
		}

		return null;
	}



	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//TODO  Implement this

		// Declare the labels for block
		Label startBlock = new Label();
		Label endBlock = new Label();

		mv.visitLineNumber(block.getFirstToken().getLinePos().line, startBlock);
		mv.visitLabel(startBlock);

		// Visit the decs and statements of block
		ArrayList<Dec> ald = block.getDecs();
		for (Dec d: ald){
			d.visit(this, mv);
		}

		ArrayList<Statement> als = block.getStatements();
		for (Statement s: als){

			s.visit(this, mv);

			try {
				BinaryChain bc = (BinaryChain)s;
				mv.visitInsn(POP);

			}
			catch(Exception e) {

			}

		}

		// visit end of block
		mv.visitLineNumber(0, endBlock);
		mv.visitLabel(endBlock);

		// visit the local variables
		for (Dec d: ald)
		{
			String identText = d.getIdent().getText();
			TypeName decType = d.getType();
			mv.visitLocalVariable(identText, decType.getJVMTypeDesc(), null,
					startBlock, endBlock, d.getSlot());
			slot--;
		}

		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {

		//TODO Implement this
		/*Boolean val = booleanLitExpression.getValue();
		mv.visitLdcInsn(val);

		return null;*/
		if (booleanLitExpression.getValue()) {
			mv.visitInsn(ICONST_1);
		} else {
			mv.visitInsn(ICONST_0);
		}
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {

		switch(constantExpression.getFirstToken().kind) {
		case KW_SCREENWIDTH:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName,
					"getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);

			break;
		case KW_SCREENHEIGHT:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName,
					"getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);

			break;

		}
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {

		declaration.setSlot(slot++);
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {

		switch(filterOpChain.firstToken.kind){
		case OP_BLUR:
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName,
					"blurOp", PLPRuntimeFilterOps.opSig, false);
			break;
		case OP_GRAY:
			if (!arg.equals("bararrow")) {

				mv.visitInsn(ACONST_NULL);

			}
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName,
						"grayOp", PLPRuntimeFilterOps.opSig, false);


			break;
		case OP_CONVOLVE:
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName,
					"convolveOp", PLPRuntimeFilterOps.opSig, false);
			break;
		default:
			break;
		}

		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {

		List<Expression> loe = frameOpChain.getArg().getExprList();
		for (Expression e : loe) {
			e.visit(this, arg);
		}

		switch(frameOpChain.firstToken.kind){
		case KW_SHOW:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName,
					"showImage", PLPRuntimeFrame.showImageDesc, false);
			break;
		case KW_HIDE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName,
					"hideImage", PLPRuntimeFrame.hideImageDesc, false);
			break;
		case KW_MOVE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName,
					"moveFrame", PLPRuntimeFrame.moveFrameDesc, false);
			break;
		case KW_XLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName,
					"getXVal", PLPRuntimeFrame.getXValDesc, false);
			break;
		case KW_YLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName,
					"getYVal", PLPRuntimeFrame.getYValDesc, false);
			break;
		default:
			break;
		}

		return null;

	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {

		Dec decl = identChain.getDec();
		TypeName type = identChain.getType();
		String text = identChain.getFirstToken().getText();

		// if on left side
		if(arg.equals("left")) {

			// if class variable
			if (decl instanceof ParamDec)
			{
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className,text, type.getJVMTypeDesc());

			}
			// if local variable
			else
			{
				if (type.isType(TypeName.INTEGER) || type.isType(TypeName.BOOLEAN))
					mv.visitVarInsn(ILOAD, decl.getSlot());

				else {
					switch(type){
					case FRAME:
						if (identChain.getDec().isFrameSet())
							mv.visitVarInsn(ALOAD, decl.getSlot());
						else
							mv.visitInsn(ACONST_NULL);
						break;
					default:
						mv.visitVarInsn(ALOAD, decl.getSlot());
						break;
					}
				}
			}
		}
		// if on right side
		else  {

			switch(type){
			case FILE:
				// if a class variable
				if(identChain.getDec() instanceof ParamDec){
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className,
							text,type.getJVMTypeDesc());
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write",
							PLPRuntimeImageIO.writeImageDesc, false);
					identChain.getDec().setFrame(true);
				}
				// if a local variable
				else {
					mv.visitVarInsn(ALOAD, identChain.getDec().getSlot());
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "write",
							PLPRuntimeImageIO.writeImageDesc, false);
					identChain.getDec().setFrame(true);
				}


				break;
			case FRAME:
				if (identChain.getDec().isFrameSet()) {

					// if a local variable
					mv.visitVarInsn(ALOAD, decl.getSlot());
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName,
							"createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
					mv.visitVarInsn(ASTORE, decl.getSlot());
				}
				else {
					mv.visitInsn(ACONST_NULL);
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "createOrSetFrame",
							PLPRuntimeFrame.createOrSetFrameSig, false);
					mv.visitVarInsn(ASTORE, identChain.getDec().getSlot());
					identChain.getDec().setFrame(true);
				}
				break;
			case INTEGER:
				// if a class variable
				if (identChain.getDec() instanceof ParamDec) {
					mv.visitVarInsn(ALOAD, 0);
					mv.visitInsn(SWAP);
					mv.visitFieldInsn(PUTFIELD, className,
							text,type.getJVMTypeDesc());

					identChain.getDec().setFrame(true);
				}
				// if a local variable
				else {
					mv.visitVarInsn(ISTORE, decl.getSlot());
					identChain.getDec().setFrame(true);
				}
				break;
			case IMAGE:
				mv.visitVarInsn(ASTORE, decl.getSlot());
				identChain.getDec().setFrame(true);

				break;
			default:
				break;
			}
		}

		return null;
	}


	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		//TODO Implement this

		Dec decl = identExpression.getDec();

		if (decl instanceof ParamDec)
		{
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, identExpression.getFirstToken().getText(), decl.getType().getJVMTypeDesc());
		}
		else
		{
			if (identExpression.getType().isType(TypeName.INTEGER)
					|| identExpression.getType().isType(TypeName.BOOLEAN)) {
				mv.visitVarInsn(ILOAD, decl.getSlot());
			}
			else {
				mv.visitVarInsn(ALOAD, decl.getSlot());
			}
		}

		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		//TODO Implement this

		Dec decl = identX.getDec();
		TypeName type = identX.getDec().getType();
		String text = identX.getText();

		if (decl instanceof ParamDec)
		{
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, text,type.getJVMTypeDesc());
		}
		else
		{
			switch(decl.getType()){
			case IMAGE:
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName,
						"copyImage",PLPRuntimeImageOps.copyImageSig, false);
				mv.visitVarInsn(ASTORE, decl.getSlot());
				decl.setFrame(true);
				break;
			case INTEGER:
			case BOOLEAN:
				mv.visitVarInsn(ISTORE, decl.getSlot());
				decl.setFrame(true);
				break;
			default:
				mv.visitVarInsn(ASTORE, decl.getSlot());
				decl.setFrame(true);
				break;
			}
		}

		return null;

	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		//TODO Implement this

		Expression e = ifStatement.getE();
		e.visit(this, arg);

		Label elseLabel = new Label();

		mv.visitJumpInsn(IFEQ, elseLabel);

		Block b = ifStatement.getB();
		b.visit(this, arg);

		mv.visitLabel(elseLabel);

		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {

		List<Expression> loe = imageOpChain.getArg().getExprList();
		for (Expression e : loe) {
			e.visit(this, arg);
		}
		switch(imageOpChain.firstToken.kind){
		case OP_WIDTH:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage",
					"getWidth", "()I", false);
			break;
		case OP_HEIGHT:
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/awt/image/BufferedImage",
					"getHeight", "()I", false);
			break;
		case KW_SCALE:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName,
					"scale", PLPRuntimeImageOps.scaleSig, false);
			break;
		default:
			break;
		}

		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn(intLitExpression.value);

		return null;
	}


	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans

		String identText = paramDec.getIdent().getText();
		TypeName paramType = paramDec.getType();

		FieldVisitor fv = cw.visitField(ACC_PUBLIC, identText,
				paramType.getJVMTypeDesc(), null, null);
		fv.visitEnd();

		switch(paramType) {

		case BOOLEAN:

			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);

			mv.visitLdcInsn(currDecSlot++);

			mv.visitInsn(AALOAD);

			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean",
					"parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTFIELD, className, identText,
					paramType.getJVMTypeDesc());
			break;

		case INTEGER:

			mv.visitVarInsn(ALOAD, 0);

			mv.visitVarInsn(ALOAD, 1);

			mv.visitLdcInsn(currDecSlot++);

			mv.visitInsn(AALOAD);

			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer",
					"parseInt", "(Ljava/lang/String;)I", false);

			mv.visitFieldInsn(PUTFIELD, className, identText ,
					paramType.getJVMTypeDesc());
			break;

		case URL:

			mv.visitVarInsn(ALOAD, 0);

			mv.visitVarInsn(ALOAD, 1);

			mv.visitLdcInsn(currDecSlot++);

			mv.visitMethodInsn(INVOKESTATIC,
					PLPRuntimeImageIO.className,"getURL",
					PLPRuntimeImageIO.getURLSig, false);
			mv.visitFieldInsn(PUTFIELD, className, identText,
					paramType.getJVMTypeDesc());
			break;

		case FILE:

			mv.visitVarInsn(ALOAD, 0);

			mv.visitTypeInsn(NEW, "java/io/File");

			mv.visitInsn(DUP);

			mv.visitVarInsn(ALOAD, 1);

			mv.visitLdcInsn(currDecSlot++);

			mv.visitInsn(AALOAD);

			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>",
					"(Ljava/lang/String;)V", false);

			mv.visitFieldInsn(PUTFIELD, className, identText,
					paramType.getJVMTypeDesc());
			break;
		default:
			break;

		}

		return null;

	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {

		mv.visitLdcInsn(sleepStatement.getE().getVal());
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);

		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {

		List<Expression> loe = tuple.getExprList();
		for (Expression e : loe) {
			e.visit(this, arg);
		}

		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		//TODO Implement this

		Label whileGuard = new Label();
		mv.visitJumpInsn(GOTO, whileGuard);

		Label whileBody = new Label();
		mv.visitLabel(whileBody);

		Block b = whileStatement.getB();
		b.visit(this, null);

		mv.visitLabel(whileGuard);

		Expression e = whileStatement.getE();
		e.visit(this, null);

		mv.visitJumpInsn(IFNE, whileBody);

		return null;
	}

}

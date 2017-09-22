package cop5556sp17;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

public class Scanner {

	/**
	 * Kind enum
	 */

	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), KW_IMAGE("image"), KW_URL("url"),
		KW_FILE("file"), KW_FRAME("frame"), KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"),
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), RBRACE("}"), ARROW("->"), BARARROW("|->"),
		OR("|"), AND("&"), EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), PLUS("+"), MINUS("-"),
		TIMES("*"), DIV("/"), MOD("%"), NOT("!"), ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"),
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), OP_WIDTH("width"), OP_HEIGHT("height"),
		KW_XLOC("xloc"), KW_YLOC("yloc"), KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"),
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}

	/**
	 * Thrown by Scanner when an illegal character is encountered
	 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}

	/**
	 * Thrown by Scanner when an int literal is not a value that can be
	 * represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
		public IllegalNumberException(String message) {
			super(message);
		}
	}

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;

		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}

	public class Token {
		public final Kind kind;
		public final int pos; // position in input array
		public final int length;

		// returns the text of this Token
		public String getText() {
			// TODO IMPLEMENT THIS
			// return the text of token saved in the textMap by retrieving the values associated with this.pos key
			return textMap.get(this.pos);
		}

		// returns a LinePos object representing the line and column of this
		// Token
		LinePos getLinePos() {
			// TODO IMPLEMENT THIS
			int line = 0;
			// Perform binary search on the lineArr to get the line number of the token
			line = Collections.binarySearch(lineArr, this.pos);
			// if this.pos is found in lineArr, get its index else get the index of the greatest value less than this.pos
			if (line >= 0){}
			else line = Math.abs(line + 2);
			return new LinePos(line, this.pos - lineArr.get(line) - 1);
		}

		public boolean isKind(Kind...kinds) {
			for(int i=0; i<kinds.length; i++){
				if (this.kind.equals(kinds[i])) {
					return true;
				}
			}
			return false;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/**
		 * Precondition: kind = Kind.INT_LIT, the text can be represented with a
		 * Java int. Note that the validity of the input should have been
		 * checked when the Token was created. So the exception should never be
		 * thrown.
		 *
		 * @return int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException {
			// TODO IMPLEMENT THIS
			int int_val = 0;
			try {
				int_val = Integer.parseInt(this.getText());
			} catch (NumberFormatException e) {}
			return int_val;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + pos;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Token)) {
				return false;
			}
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (kind != other.kind) {
				return false;
			}
			if (length != other.length) {
				return false;
			}
			if (pos != other.pos) {
				return false;
			}
			return true;
		}



		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
	}

	/**
	 * Initializes Scanner object by traversing chars and adding tokens to
	 * tokens list.
	 *
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {

		// TODO IMPLEMENT THIS!!!!

		// Variables declaration
		int pos = 0;
		int length = chars.length();
		int startPos = 0;
		int ch;
		String state = "START";
		// flag to decide the token to which '-' belongs to
		int minus_f = 0;
		// flag to decide the token to which '*' belongs to
		int times_f = 0;

		// Data structures declaration
		// reserved words hashMap
		HashMap<String, Kind> rMap = new HashMap<String, Kind>();
		/* Adding elements to HashMap */
		rMap.put(Kind.KW_INTEGER.getText(), Kind.KW_INTEGER);
		rMap.put(Kind.KW_BOOLEAN.getText(), Kind.KW_BOOLEAN);
		rMap.put(Kind.KW_IMAGE.getText(), Kind.KW_IMAGE);
		rMap.put(Kind.KW_URL.getText(), Kind.KW_URL);
		rMap.put(Kind.KW_FILE.getText(), Kind.KW_FILE);
		rMap.put(Kind.KW_FRAME.getText(), Kind.KW_FRAME);
		rMap.put(Kind.KW_WHILE.getText(), Kind.KW_WHILE);
		rMap.put(Kind.KW_IF.getText(), Kind.KW_IF);
		rMap.put(Kind.OP_SLEEP.getText(), Kind.OP_SLEEP);
		rMap.put(Kind.KW_SCREENHEIGHT.getText(), Kind.KW_SCREENHEIGHT);
		rMap.put(Kind.KW_SCREENWIDTH.getText(), Kind.KW_SCREENWIDTH);
		rMap.put(Kind.OP_GRAY.getText(), Kind.OP_GRAY);
		rMap.put(Kind.OP_CONVOLVE.getText(), Kind.OP_CONVOLVE);
		rMap.put(Kind.KW_SCALE.getText(), Kind.KW_SCALE);
		rMap.put(Kind.OP_WIDTH.getText(), Kind.OP_WIDTH);
		rMap.put(Kind.OP_HEIGHT.getText(), Kind.OP_HEIGHT);
		rMap.put(Kind.KW_XLOC.getText(), Kind.KW_XLOC);
		rMap.put(Kind.KW_YLOC.getText(), Kind.KW_YLOC);
		rMap.put(Kind.KW_HIDE.getText(), Kind.KW_HIDE);
		rMap.put(Kind.KW_SHOW.getText(), Kind.KW_SHOW);
		rMap.put(Kind.KW_MOVE.getText(), Kind.KW_MOVE);
		rMap.put(Kind.KW_TRUE.getText(), Kind.KW_TRUE);
		rMap.put(Kind.KW_FALSE.getText(), Kind.KW_FALSE);
		rMap.put(Kind.OP_BLUR.getText(), Kind.OP_BLUR);
		rMap.put("0", Kind.INT_LIT);
		rMap.put(Kind.SEMI.getText(), Kind.SEMI);
		rMap.put(Kind.COMMA.getText(), Kind.COMMA);
		rMap.put(Kind.LPAREN.getText(), Kind.LPAREN);
		rMap.put(Kind.RPAREN.getText(), Kind.RPAREN);
		rMap.put(Kind.LBRACE.getText(), Kind.LBRACE);
		rMap.put(Kind.RBRACE.getText(), Kind.RBRACE);
		rMap.put(Kind.AND.getText(), Kind.AND);
		rMap.put(Kind.PLUS.getText(), Kind.PLUS);
		rMap.put(Kind.MOD.getText(), Kind.MOD);

		// Scan the input string character by character and keep saving the tokens appropriately
		while (pos <= length) {
			ch = pos < length ? chars.charAt(pos) : -1;

			switch (state) {

			// case "START"
			case "START": {
				// initialize
				minus_f = 0;
				times_f = 0;
				startPos = pos;

				switch (ch) {

				case -1: {
					tokens.add(new Token(Kind.EOF, startPos, Kind.EOF.getText().length()));
					textMap.put(startPos, Kind.EOF.getText());
					pos++;
				}
				break;
				// cases handling if ch is part of a multiple character token
				case '*': {
					state = "AFTER_*";
					pos++;
				}
				break;
				case '/': {
					state = "AFTER_/";
					pos++;
				}
				break;
				case '|': {
					state = "AFTER_|";
					pos++;
				}
				break;
				case '-': {
					state = "AFTER_-";
					pos++;
				}
				break;
				case '=': {
					state = "AFTER_=";
					pos++;
				}
				break;
				case '!': {
					state = "AFTER_!";
					pos++;
				}
				break;
				case '<': {
					state = "AFTER_<";
					pos++;
				}
				break;
				case '>': {
					state = "AFTER_>";
					pos++;
				}
				break;

				default:
					// if ch is any single character token
					if (rMap.containsKey(String.valueOf((char) ch))) {
						tokens.add(new Token(rMap.get(String.valueOf((char) ch)), startPos, 1));
						textMap.put(startPos, String.valueOf(chars.charAt(startPos)));
						pos++;
					}

					// if ch is a digit
					else if (Character.isDigit(ch)) {
						state = "IN_DIGIT";
						pos++;
					}

					// if ch is an identifier start
					else if (Character.isJavaIdentifierStart(ch)) {
						state = "IN_IDENT";
						pos++;
					}
					// if ch is a white space character
					else if (Character.isWhitespace(ch)) {
						// if a new line is encountered, track the line numbers of tokens by storing \n position in lineArr
						if (ch == '\n') {
							lineArr.add(pos);
						}
						state = "START";
						pos++;
					}
					// if ch is an Illegal character
					else {
						throw new IllegalCharException("Illegal character encountered:"+chars.charAt(pos)+"at position:"+pos);
					}
				}
			}
			break;

			// case "IN_IDENT"
			case "IN_IDENT": {
				// do greedy tokenizing, keep on concatenating until you encounter a character which cannot be part of identifier.
				if (Character.isJavaIdentifierPart(ch))
					pos++;
				else {
					// check if the token being scanned belongs to any of the reserved words in rMap else save it as an IDENT token.
					String matchId = chars.substring(startPos, pos);
					if (rMap.containsKey(matchId)) {
						tokens.add(new Token(rMap.get(matchId), startPos, pos - startPos));
						textMap.put(startPos, matchId);
					} else {
						tokens.add(new Token(Kind.IDENT, startPos, pos - startPos));
						textMap.put(startPos, matchId);
					}
					state = "START";
				}
			}
			break;

			// case "IN_DIGIT"
			case "IN_DIGIT": {
				if (Character.isDigit(ch))
					pos++;
				else {
					// check if the INT_LIT token being scanned is a valid JAVA int value before saving it
					String matchInt = chars.substring(startPos, pos);
					try {
						Integer.parseInt(matchInt);
						tokens.add(new Token(Kind.INT_LIT, startPos, pos - startPos));
						textMap.put(startPos, chars.substring(startPos, pos));
						state = "START";
					} catch (NumberFormatException e) {
						throw new IllegalNumberException(
								"Illegal integer literal token encountered:" + matchInt + "at position:" + startPos);
					}
				}
			}
			break;

			// case "AFTER_/"
			case "AFTER_/": {
				// if /* exists continue to scan the token else save it as DIV token
				if (ch == '*') {
					state = "AFTER_/*";
					pos++;

				} else {
					tokens.add(new Token(Kind.DIV, startPos, pos - startPos));
					textMap.put(startPos, String.valueOf(chars.charAt(startPos)));
					state = "START";
				}
			}
			break;

			// case "AFTER_/*"
			case "AFTER_/*": {
				// if /** continue scanning to check for / else include the following characters in open comment till EOF is reached
				if (ch == '*') {
					pos++;
					ch = pos < length ? chars.charAt(pos) : -1;
					if (ch == '/'){
						state = "START";
					}
					pos++;

				} else {
					if (pos < length)
					{
						if (ch == '\n') {
							lineArr.add(pos);
						}
						pos++;
					}
					else
						state = "START";
				}
			}
			break;


			// case "AFTER_*"
			case "AFTER_*": {
				// if */ exists mark as end of comment if in open comment else save it as TIMES token
				if (ch == '/') {
					if(times_f == 1) {
						state = "START";
						pos++;
					}
					else {
						tokens.add(new Token(Kind.TIMES, startPos, pos - startPos));
						textMap.put(startPos, String.valueOf(chars.charAt(startPos)));
						state = "START";
					}
				} else {
					tokens.add(new Token(Kind.TIMES, startPos, pos - startPos));
					textMap.put(startPos, chars.substring(startPos, pos));
					state = "START";
				}
			}
			break;

			// case "AFTER_|"
			case "AFTER_|": {
				// if |- exists, check for BARARROW token else save | as OR token
				if (ch == '-') {
					state = "AFTER_|-";
					pos++;
				} else {
					tokens.add(new Token(Kind.OR, startPos, pos - startPos));
					textMap.put(startPos, chars.charAt(startPos) + "");
					state = "START";
				}
			}
			break;

			// case "AFTER_|-"
			case "AFTER_|-": {
				// if |-> exists save it as BARARROW token else continue scanning characters after -
				if (ch == '>') {
					tokens.add(new Token(Kind.BARARROW, startPos, pos - startPos + 1));
					textMap.put(startPos, chars.substring(startPos, pos + 1));
					state = "START";
					pos++;
				} else {
					minus_f = 1;
					state = "AFTER_-";
				}
			}
			break;

			// case "AFTER_-"
			case "AFTER_-": {
				// if -> exists save it as ARROW token else if |- exists save them as OR and MINUS tokens else only save MINUS token
				if (ch == '>') {
					tokens.add(new Token(Kind.ARROW, startPos, pos - startPos + 1));
					textMap.put(startPos, chars.substring(startPos, pos + 1));
					state = "START";
					pos++;
				} else {
					if (minus_f == 1) {
						tokens.add(new Token(Kind.OR, startPos, 1));
						textMap.put(startPos, String.valueOf(chars.charAt(startPos)));
						tokens.add(new Token(Kind.MINUS, startPos + 1, 1));
						textMap.put(startPos + 1, String.valueOf(chars.charAt(startPos + 1)));

					} else {
						tokens.add(new Token(Kind.MINUS, startPos, 1));
						textMap.put(startPos, String.valueOf(chars.charAt(startPos)));
					}
					state = "START";
				}
			}
			break;

			// case "AFTER_="
			case "AFTER_=": {
				// if == exists save it as EQUAL token else declare = as invalid character
				if (ch == '=') {
					tokens.add(new Token(Kind.EQUAL, startPos, pos - startPos + 1));
					textMap.put(startPos, chars.substring(startPos, pos + 1));
					state = "START";
					pos++;
				} else
					throw new IllegalCharException(
							"Illegal character encountered:" + chars.charAt(startPos) + "at position:" + startPos);
			}
			break;

			// case "AFTER_!"
			case "AFTER_!": {
				// if != exists save it as NOTEQUAL token else save it as NOT token
				if (ch == '=') {
					tokens.add(new Token(Kind.NOTEQUAL, startPos, pos - startPos + 1));
					textMap.put(startPos, chars.substring(startPos, pos + 1));
					state = "START";
					pos++;
				} else {
					tokens.add(new Token(Kind.NOT, startPos, 1));
					textMap.put(startPos, String.valueOf(chars.charAt(startPos)));
					state = "START";
				}
			}
			break;

			// case "AFTER_<"
			case "AFTER_<": {
				// if <- exists save it as ASSIGN token else if <= exists save it as LE token, else save it as LT token
				if (ch == '-') {
					tokens.add(new Token(Kind.ASSIGN, startPos, pos - startPos+1));
					textMap.put(startPos, chars.substring(startPos, pos+1));
					state = "START";
					pos++;
				} else if (ch == '=') {
					tokens.add(new Token(Kind.LE, startPos, pos - startPos + 1));
					textMap.put(startPos, chars.substring(startPos, pos + 1));
					state = "START";
					pos++;
				} else {
					tokens.add(new Token(Kind.LT, startPos, 1));
					textMap.put(startPos, String.valueOf(chars.charAt(startPos)));
					state = "START";
				}
			}
			break;

			// case "AFTER_>"
			case "AFTER_>": {
				// if >= exists save it as GE token token, else if > exists save it as GT token
				if (ch == '=') {
					tokens.add(new Token(Kind.GE, startPos, pos - startPos + 1));
					textMap.put(startPos, chars.substring(startPos, pos + 1));
					state = "START";
					pos++;
				} else {
					tokens.add(new Token(Kind.GT, startPos, 1));
					textMap.put(startPos, String.valueOf(chars.charAt(startPos)));
					state = "START";
				}
			}
			break;

			default:
				assert false;
			}
		}

		return this;
	}

	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;
	// ArrayList to store pos of new line characters encountered
	ArrayList<Integer> lineArr = new ArrayList<Integer>(Collections.nCopies(1, -1));
	// Token text hashMap
	HashMap<Integer, String> textMap = new HashMap<Integer, String>();

	/*
	 * Return the next token in the token list and update the state so that the
	 * next call will return the Token..
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}

	/*
	 * Return the next token in the token list without updating the state. (So
	 * the following call to next will return the same token.)
	 */
	public Token peek() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);
	}

	/**
	 * Returns a LinePos object containing the line and position in line of the
	 * given token.
	 *
	 * Line numbers start counting at 0
	 *
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		// TODO IMPLEMENT THIS
		// return null;
		return t.getLinePos();
	}



}

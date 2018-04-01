package lexer;

import static control.Control.ConLexer.dump;

import java.io.InputStream;
import java.util.HashMap;

import lexer.Token.Kind;
import util.Todo;

public class Lexer
{
  String fname; // the input file name to be compiled
  InputStream fstream; // input stream for the above file
  static int linenum =1;
  static HashMap<String, Kind> tokenPool;
  public Lexer(String fname, InputStream fstream)
  {
	 if(tokenPool == null)initToken();
    this.fname = fname;
    this.fstream = fstream;
  }
  private void initToken() {
	  tokenPool = new HashMap<>();
	  tokenPool.put(String.valueOf('+'), Kind.TOKEN_ADD);
	  tokenPool.put(new String("&&"), Kind.TOKEN_AND);
	  tokenPool.put(String.valueOf('='), Kind.TOKEN_ASSIGN);
	  tokenPool.put(new String("boolean"), Kind.TOKEN_BOOLEAN);
	  tokenPool.put(new String("class"), Kind.TOKEN_CLASS);
	  tokenPool.put(new String(","), Kind.TOKEN_COMMER);
	  tokenPool.put(new String("/"), Kind.TOKEN_DIV);
	  tokenPool.put(new String("."), Kind.TOKEN_DOT);
	  tokenPool.put(new String("else"), Kind.TOKEN_ELSE);
	  tokenPool.put(new String("extends"), Kind.TOKEN_EXTENDS);
	  tokenPool.put(new String("method"), Kind.TOKEN_FUNCTION);
	  tokenPool.put(new String("false"), Kind.TOKEN_FALSE);
	  tokenPool.put(new String("if"), Kind.TOKEN_IF);
	  tokenPool.put(new String("int"), Kind.TOKEN_INT);
	  tokenPool.put(new String("{"), Kind.TOKEN_LBRACE);
	  tokenPool.put(new String("["), Kind.TOKEN_LBRACK);
	  tokenPool.put(new String("length"), Kind.TOKEN_LENGTH);
	  tokenPool.put(new String("long"), Kind.TOKEN_LONG);
	  tokenPool.put(new String("("), Kind.TOKEN_LPAREN);
	  tokenPool.put(new String("<"), Kind.TOKEN_LT);
	  tokenPool.put(new String("main"), Kind.TOKEN_MAIN);
	  tokenPool.put(new String("new"), Kind.TOKEN_NEW);
	  tokenPool.put(new String("!"), Kind.TOKEN_NOT);
//	  tokenPool.put(new String("NUM"), Kind.TOKEN_DIV);
	  tokenPool.put(new String("out"), Kind.TOKEN_OUT);
	  tokenPool.put(new String("println"), Kind.TOKEN_PRINTLN);
	  tokenPool.put(new String("public"), Kind.TOKEN_PUBLIC);
	  tokenPool.put(new String("}"), Kind.TOKEN_RBRACE);
	  tokenPool.put(new String("]"), Kind.TOKEN_RBRACK);
	  tokenPool.put(new String("return"), Kind.TOKEN_RETURN);
	  tokenPool.put(new String(")"), Kind.TOKEN_RPAREN);
	  tokenPool.put(new String(";"), Kind.TOKEN_SEMI);
	  tokenPool.put(new String("static"), Kind.TOKEN_STATIC);
	  tokenPool.put(new String("String"), Kind.TOKEN_STRING);
	  tokenPool.put(new String("-"), Kind.TOKEN_SUB);
	  tokenPool.put(new String("System"), Kind.TOKEN_SYSTEM);
	  tokenPool.put(new String("this"), Kind.TOKEN_THIS);
	  tokenPool.put(new String("*"), Kind.TOKEN_TIMES);
	  tokenPool.put(new String("true"), Kind.TOKEN_TRUE);
	  tokenPool.put(new String("void"), Kind.TOKEN_VOID);
	  tokenPool.put(new String("while"), Kind.TOKEN_WHILE);
  }
  // When called, return the next token (refer to the code "Token.java")
  // from the input stream.
  // Return TOKEN_EOF when reaching the end of the input stream.
  private Token nextTokenInternal() throws Exception
  {
	  
    int c = this.fstream.read();
//    if (-1 == c)
//      // The value for "lineNum" is now "null",
//      // you should modify this to an appropriate
//      // line number for the "EOF" token.
//      return new Token(Kind.TOKEN_EOF, linenum);
    // skip all kinds of "blanks"
    while (' ' == c || '\t' == c || c=='\n') {
    	if(c=='\n')linenum++;
    	c = this.fstream.read();
    }
    if (-1 == c)
      return new Token(Kind.TOKEN_EOF, linenum);
    
    String s  = String.valueOf((char)c);
    switch (c) {
    case '+':
      return new Token(Kind.TOKEN_ADD, linenum);
     
    default:
      // Lab 1, exercise 2: supply missing code to
      // lex other kinds of tokens.
      // Hint: think carefully about the basic
      // data structure and algorithms. The code
      // is not that much and may be less than 50 lines. If you
      // find you are writing a lot of code, you
      // are on the wrong way.
      new Todo();
      return null;
    }
  }

  public Token nextToken()
  {
    Token t = null;

    try {
      t = this.nextTokenInternal();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    if (dump)
      System.out.println(t.toString());
    return t;
  }
}
